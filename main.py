import sys
import logging
from argparse import ArgumentParser
from pepper import workflow

def init_parser():
    parser = ArgumentParser(description='ODM data extractor')
    parser.add_argument('--input', '-i', help='path ODM file with all events for selected frame', required=True)
    parser.add_argument('--template', '-t', help='path to template file', required=True)
    parser.add_argument('--output', '-o', help='path to output file', required=True)
    parser.add_argument('--loglevel', '-l', help='logging level', default='error', choices=['info', 'debug', 'error'])
    return parser


def test_workflow():
    s = (' -i ../resources/odm1.3_full033_Hematology_2015-06-08-115931798.xml'
         ' -t ../resources/hematology.csv'
         ' -o ../resources/hematology_results.csv'
         ' -l debug')
    sys.argv = [sys.argv[0]] + s.split()
    parser = init_parser()
    args = parser.parse_args()
    setup_logger(args)
    workflow(args.input, args.template, args.output)


def setup_logger(args):
    logger = logging.getLogger('pepper')
    loglevel = {'info': logging.INFO, 'debug': logging.DEBUG, 'error': logging.ERROR}.get(args.loglevel)
    logger.setLevel(level=loglevel)


def main():
    parser = init_parser()
    args = parser.parse_args()
    setup_logger(args)
    workflow(args.input, args.template, args.output)

if __name__ == '__main__':
    main()

