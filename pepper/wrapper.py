from lxml import etree
import logging
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
                                key = (subject_id, event_id, form_id, item_data.get('ItemOID'))
                                if self.data.get((subject_id, event_id, form_id, item_data.get('ItemOID'))) is None:
                                    self.data[key] = [item_data.get('Value').strip()]
                                else:
                                    self.data[key].append(item_data.get('Value').strip())
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
        subject_blocks = []
        event_line = []   # empty cell for SubjectID holding
        item_line = []    # empty cell for SubjectID holding
        for item_id in template.items:
            for event_id in template.events:
                item_line.append(item_id.strip())
                event_line.append(event_id.strip())
        for subject_id in template.subjects:
            block_lines = 1
            for item_id in template.items:
                for event_id in template.events:
                    for form_id in template.forms:
                        replications = self.data.get((subject_id, event_id, form_id, item_id))
                        if replications is not None and len(replications) > block_lines:
                            block_lines = len(replications)
            subject_block = [[subject_id for _ in range(block_lines)]]
            for item_id in template.items:
                item_status_id = None
                if item_id.endswith('RES'):
                    item_status_id = item_id[:-3] + 'YN'    # make IGEYN from IGERES
                for event_id in template.events:
                    coll = []
                    replications = None
                    for form_id in template.forms:
                        if replications is None:
                            replications = self.data.get((subject_id, event_id, form_id, item_id))
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
                    subject_block.append(coll)
            subject_blocks.append(subject_block)

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
        lines = [sep.join(line)+'\n' for line in table]
        with codecs.open(output_file, 'w', 'utf-8') as fp:
            fp.writelines(lines)


def wrapper_from_file(file_path):
    parser = etree.XMLParser(encoding='utf-8')
    with codecs.open(file_path, 'r', 'utf-8') as fp:
        tree = etree.parse(fp, parser)
    return ODMWrapper(tree.getroot())
