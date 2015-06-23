import logging
import os
from .template import template_from_file
from .wrapper import wrapper_from_file

logger = logging.getLogger(__package__)
logger.addHandler(logging.FileHandler(__package__+'.log'))

def workflow(input_filename, template_filename, output_filename, rename_items=False, rename_events=False):
    if not os.path.exists(input_filename):
        error = 'No input file found: ' + input_filename
        logger.error(error)
        raise IOError(error)
    if not os.path.exists(template_filename):
        error = 'No input file found: ' + template_filename
        logger.error(error)
        raise IOError(error)
    template = template_from_file(template_filename)
    wrapper = wrapper_from_file(input_filename)
    wrapper.process_to_file(template, output_filename, rename_items, rename_events)
