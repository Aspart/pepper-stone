class Block:
    def __init__(self, id, lines):
        self.id = id
        self.lines = lines
        self.data = [[id for _ in range(lines)]]

    def add_column(self, col):
        if len(col) != self.lines:
            raise ValueError('Wrong lines count in data column')
        self.data.append(col)
