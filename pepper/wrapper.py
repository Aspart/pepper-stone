import logging

from lxml import etree
from .block import Block
import codecs

logger = logging.getLogger(__package__)


class ODMWrapper:
    """
    Organize ODM container into data structure: (StudyEventOID, FormOID, ItemOID) = Replications
    :param file_path:
    :return:
    """

    def __init__(self, root):
        self.root = root
        self.data = {}
        self.groups = {}
        oc_namespace = self.root.nsmap['OpenClinica']
        for clinical_data in self.root.findall('{*}ClinicalData'):
            for subject_data in clinical_data.findall('{*}SubjectData'):
                subject_id = subject_data.attrib.get('{%s}StudySubjectID' % oc_namespace)
                for event in subject_data.findall('{*}StudyEventData'):
                    event_id = event.attrib.get('StudyEventOID')
                    for form_data in event.findall('{*}FormData'):
                        form_id = form_data.attrib['FormOID']
                        for item_group_data in form_data.findall('{*}ItemGroupData'):
                            for item_data in item_group_data.findall('{*}ItemData'):
                                groups = self.groups.get(subject_id)
                                if groups is None:
                                    groups = 0
                                group_id = item_group_data.attrib.get('ItemGroupRepeatKey')
                                if group_id is None:
                                    group_id = 1
                                else:
                                    group_id = int(group_id)
                                if group_id > groups:
                                    self.groups[subject_id] = group_id
                                key = (subject_id, event_id, form_id, item_data.get('ItemOID'), group_id)
                                value = item_data.get('Value', "").strip()
                                if self.data.get(key) is None:
                                    self.data[key] = value
                                else:
                                    value_previous = self.data.get(key)
                                    error = 'Different values for key %s: %s and %s' % (
                                    ",".join([str(x) for x in key]), value_previous, value)
                                    logger.error(error)
                                    raise ValueError(error)
        self.item_names = self.get_items_descr()
        self.event_names = self.get_events_descr()

    def get_items_descr(self):
        col_descr = {}
        for study in self.root.findall("{*}Study"):
            for meta in study.findall('{*}MetaDataVersion'):
                for element in meta.findall('{*}ItemDef'):
                    col_descr[element.attrib['OID']] = element.attrib['Comment'].strip()
        return col_descr

    def get_events_descr(self):
        event_descr = {}
        for study in self.root.findall("{*}Study"):
            for meta_data_version in study.findall('{*}MetaDataVersion'):
                for element in meta_data_version.findall('{*}StudyEventDef'):
                    event_descr[element.attrib['OID']] = element.attrib['Name'].strip()
        return event_descr

    def get_forms(self):
        forms = []
        for study in self.root.findall('{*}Study'):
            for meta_data_version in study.findall('{*}MetaDataVersion'):
                for item_group_def in meta_data_version.findall('{*}FormDef'):
                    forms.append(item_group_def.attrib['OID'].strip())
        return forms

    def get_events(self, forms=None):
        order = []
        study = self.root.find("{*}Study")
        meta = study.find('{*}MetaDataVersion')
        protocol = meta.find('{*}Protocol')
        for event in protocol.findall('{*}StudyEventRef'):
            order.append(event.attrib.get('StudyEventOID'))
        if forms is not None:
            unordered = set()
            for form_id in forms:
                searchstr = '{*}FormRef[@FormOID=\'' + form_id + '\']'
                for event in meta.findall('{*}StudyEventDef'):
                    if event.find(searchstr) is not None:
                        unordered.add(event.attrib.get('OID'))
            events = sorted(unordered, key=lambda x: order.index(x))
        else:
            events = order
        return events

    def process(self, template, rename_items=False, rename_events=False):
        """
        Retrive templated matrix from stored ODM data. Deal with it.
        :param template: from template class
        :param rename_items: rename items to its description
        :param rename_events: rename events to its description
        :return: sorted matrix
        """
        template.events = self.get_events(template.forms)
        subject_blocks = []  # empty cell for SubjectBlock holding
        event_line = []  # empty cell for SubjectID holding
        item_line = []  # empty cell for SubjectID holding
        for item_id in template.items:
            for event_id in template.events:
                item_line.append(item_id.strip())
                event_line.append(event_id.strip())
        for subject_id in template.subjects:
            # block = lines for single patient
            # num of lines = max from num of repeat events
            block_lines = self.groups.get(subject_id, 1)
            if block_lines is None:
                logger.error("Subject from template not found in data: %s" % subject_id)
            subject_block = Block(subject_id, block_lines)
            for item_id in template.items:
                item_status_id = None
                # replate empty "%value%RES" with status from "%value%YN"
                if item_id.endswith('RES'):
                    item_status_id = item_id[:-3] + 'YN'  # make %value%YN from %value%RES
                for event_id in template.events:
                    coll = []
                    replications = None
                    for form_id in template.forms:
                        if replications is None:
                            # search for value in database
                            groups = self.groups.get(subject_id)
                            if groups is None:
                                groups = 1
                            rep_buf = []
                            for group_id in range(1, groups + 1):
                                val = ""
                                for val_merge in template.merge[item_id]:
                                    if val == "":
                                        val = self.data.get((subject_id, event_id, form_id, val_merge, group_id), "")
                                rep_buf.append(val)
                            if not all(v == '' for v in rep_buf):
                                replications = rep_buf
                    if item_status_id is not None:
                        for form_id in template.forms:
                            if replications:
                                if replications[0] == '':
                                    replications = self.data.get((subject_id, event_id, form_id, item_status_id))
                            else:
                                replications = self.data.get((subject_id, event_id, form_id, item_status_id))
                    rep_count = replications and len(replications) or 0
                    for idx in range(block_lines):
                        if idx < rep_count:
                            coll.append(replications[idx])
                        else:
                            coll.append('')
                    subject_block.add_column(coll)
            subject_blocks.append(subject_block.data)

        result = []
        if rename_events:
            result.append([''] + ([self.event_names[x.replace('\xa0', '')] for x in event_line]))
        else:
            result.append([''] + event_line)
        if rename_items:
            result.append([''] + [self.item_names[x.replace('\xa0', '')] for x in item_line])
        else:
            result.append([''] + item_line)
        for subject_block in subject_blocks:
            for i in range(len(subject_block[0])):
                row = []
                for item in subject_block:
                    row.append(item[i])
                result.append(row)
        return result

    def process_to_file(self, template, output_file, rename_items=False, rename_events=False, sep=';'):
        table = self.process(template, rename_items, rename_events)
        lines = [sep.join(line) + '\n' for line in table]
        with codecs.open(output_file, 'w', 'utf-8') as fp:
            fp.writelines(lines)


def wrapper_from_file(file_path):
    parser = etree.XMLParser(encoding='utf-8')
    with codecs.open(file_path, 'r', 'utf-8') as fp:
        tree = etree.parse(fp, parser)
    return ODMWrapper(tree.getroot())
