import logging

import os
import codecs

logger = logging.getLogger(__package__)


class Template:
    """
    Template store and organize three important things (from ODM file):
    subjects - array of subject_id
    items - array of item_id
    form_id - form_id
    Help to export ODM files into reviewable representation.
    """

    def __init__(self, forms, subjects, items, merge):
        self.subjects = [x.strip() for x in subjects]
        self.forms = [x.strip() for x in forms]
        self.items = [x.strip() for x in items]
        self.events = []
        self.merge = merge


def template_from_file(path):
    if os.path.isfile(path + '.crf'):
        forms_path = path + '.crf'
    elif os.path.isfile(os.path.splitext(path)[0] + '.crf'):
        forms_path = os.path.splitext(path)[0] + '.crf'
    else:
        error = "Cannot find %s or %s file" % (path + '.crf', os.path.splitext(path)[0] + '.crf')
        logger.error(error)
        raise IOError(error)
    with codecs.open(path, 'r', 'utf-8') as fp:
        lines = fp.read().splitlines()
    table = [line.split(';') for line in lines]
    items = table[0][1:]
    subjects = [x[0] for x in table[1:]]
    replications = [x[1:] for x in table[1:]]
    merge = {}
    for idx, val in enumerate(items):
        reps = [val]
        for r in replications:
            if r[idx] != '':
                reps.append(r[idx])
        merge[val] = reps
    with open(forms_path) as fp:
        forms = fp.read().splitlines()
        if forms[0] != 'FormOID':
            error = "crf file should begin from FormOID definition"
            logger.error(error)
            raise IOError(error)
        forms = forms[1:]
    return Template(forms, subjects, items, merge)
