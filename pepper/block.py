import logging

logger = logging.getLogger(__package__)


class Block:
    def __init__(self, subject_id, lines):
        self.id = subject_id
        self.lines = lines
        self.data = [[subject_id for _ in range(lines)]]

    def add_column(self, col):
        if len(col) != self.lines:
            error = 'Wrong lines count in data column'
            logger.error(error)
            raise ValueError('Wrong lines count in data column')
        self.data.append(col)
