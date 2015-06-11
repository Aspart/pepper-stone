from argparse import ArgumentParser


def init_parser():
    parser = ArgumentParser(description='ODM data extractor')
    parser.add_argument('--in', '-i', help='path ODM file with all events for selected frame', required=True)
    parser.add_argument('--template', '-t', help='path to template file', required=True)
    parser.add_argument('--out', '-o', help='path to output file', required=True)
    return parser


def parse_args():
    return init_parser().parse_args()

if __name__ == '__main__':
    args = parse_args()
    print args
