import logging

from .template import template_from_file
from .wrapper import wrapper_from_file

logger = logging.getLogger(__package__)

def test_workflow_bc():
    template = template_from_file('../resources/biochemistry.csv')
    file_path = '../resources/odm1.3_full033_2_Biochemistry_all_2015-06-08-115516265.xml'
    wrapper = wrapper_from_file(file_path)
    wrapper.process_to_file(template, '../resources/biochemistry_results.csv')


def test_workflow_ae():
    file_path = '../resources/odm1.3_full AE_033_2015-06-02-.xml'
    template = template_from_file('../resources/adverse_effect.csv')
    wrapper = wrapper_from_file(file_path)
    wrapper.process_to_file(template, '../resources/adverse_effect_results.csv')


def test_workflow_hl():
    file_path = '../resources/odm1.3_full033_Hematology_2015-06-08-115931798.xml'
    template = template_from_file('../resources/hematology.csv')
    wrapper = wrapper_from_file(file_path)
    wrapper.process_to_file(template, '../resources/hematology_results.csv')


def test_workflow_vs():
    file_path = '../resources/odm1.3_full033_Vital_signs_2015-06-08-120601767.xml'
    template = template_from_file('../resources/vital_signs.csv')
    wrapper = wrapper_from_file(file_path)
    wrapper.process_to_file(template, '../resources/vital_signs_results.csv')

if __name__ == "__main__":
    test_workflow_bc()
    test_workflow_ae()
    test_workflow_hl()
    test_workflow_vs()
