import sys
import unittest

from main import init_parser
from main import setup_logger
from main import workflow


class WorkflowTestCase(unittest.TestCase):
    def test_workflow_edss(self):
        s = (' -i /Users/roman/Projects/Biocad/clinical-pepper-stone/resources/EDSS.xml'
             ' -t /Users/roman/Projects/Biocad/clinical-pepper-stone/resources/EDSS.csv'
             ' -o /Users/roman/Projects/Biocad/clinical-pepper-stone/resources/EDSS_results.csv'
             ' -l debug')
        sys.argv = [sys.argv[0]] + s.split()
        parser = init_parser()
        args = parser.parse_args()
        setup_logger(args)
        workflow(args.input, args.template, args.output, args.itemrename, args.eventrename)

    def test_workflow_ae(self):
            s = (' -i /Users/roman/Projects/Biocad/clinical-pepper-stone/resources/AE.xml'
                 ' -t /Users/roman/Projects/Biocad/clinical-pepper-stone/resources/AE.csv'
                 ' -o /Users/roman/Projects/Biocad/clinical-pepper-stone/resources/AE_results.csv'
                 ' -l debug')
            sys.argv = [sys.argv[0]] + s.split()
            parser = init_parser()
            args = parser.parse_args()
            setup_logger(args)
            workflow(args.input, args.template, args.output, args.itemrename, args.eventrename)

    def test_workflow_ct(self):
        s = (' -i /Users/roman/Projects/Biocad/clinical-pepper-stone/resources/CT.xml'
             ' -t /Users/roman/Projects/Biocad/clinical-pepper-stone/resources/CT.csv'
             ' -o /Users/roman/Projects/Biocad/clinical-pepper-stone/resources/CT_results.csv'
             ' -l debug')
        sys.argv = [sys.argv[0]] + s.split()
        parser = init_parser()
        args = parser.parse_args()
        setup_logger(args)
        workflow(args.input, args.template, args.output, args.itemrename, args.eventrename)

    def test_workflow_hematology(self):
        s = (' -i /Users/roman/Projects/Biocad/clinical-pepper-stone/resources/hematology.xml'
             ' -t /Users/roman/Projects/Biocad/clinical-pepper-stone/resources/hematology.csv'
             ' -o /Users/roman/Projects/Biocad/clinical-pepper-stone/resources/hematology_results.csv'
             ' -l debug')
        sys.argv = [sys.argv[0]] + s.split()
        parser = init_parser()
        args = parser.parse_args()
        setup_logger(args)
        workflow(args.input, args.template, args.output, args.itemrename, args.eventrename)

if __name__ == "__main__":
    unittest.main()
