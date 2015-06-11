import os


class Template:
    """
    Template store and organize three important things (from ODM file):
    subjects - array of subject_id
    items - array of item_id
    form_id - form_id
    Help to export ODM files into reviewable representation.
    """
    def __init__(self, forms, subjects, items):
        self.subjects = subjects
        self.forms = forms
        self.items = items
        self.events = []


def template_from_file(path):
    if os.path.isfile(path + '.crf'):
        forms_path = path + '.crf'
    elif os.path.isfile(os.path.splitext(path)[0] + '.crf'):
        forms_path = os.path.splitext(path)[0] + '.crf'
    else:
        raise ValueError("Cannot find *.crf or *.csv.crf file")
    with open(path) as fp:
        lines = fp.read().splitlines()
    items = [x for x in lines[0].split(';')][1:]
    subjects = [x.split(';')[0] for x in lines[1:]]
    with open(forms_path) as fp:
        forms = fp.read().splitlines()
        if forms[0] != 'FormOID':
            raise ValueError("crf file should begin from FormOID definition")
        forms = forms[1:]
    return Template(forms, subjects, items)
