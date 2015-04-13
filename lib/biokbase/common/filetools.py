import os as _os
import xlrd as _xlrd
import datetime as _datetime


def touch(fname, times=None):
    '''Python implementation of the unix touch command. See os.utime for the
    format of the times argument. Reference:
    http://stackoverflow.com/questions/1158076/implement-touch-using-python
    '''

    with file(fname, 'a'):
        _os.utime(fname, times)


class ExcelDictReader(object):
    '''Iterates over an Excel sheet and returns the rows as a dictionary, the
    keys of which are the entries of the first row. All columns must therefore
    have a unique header in the first row. Thin wrapper around xlrd and passes
    on most xlrd errors.
    Blank cells or cells with an empty string are returned as None.
    Dates are returned as a date string as formatted by datetime.datetime.
    Cells with an error cause and exception to be thrown.'''

    def __init__(self, excelfile, sheet=None):
        self._xl = _xlrd.open_workbook(excelfile)
        if sheet is not None:
            self.set_sheet(sheet)
        else:
            self._sheet = None

    def num_sheets(self):
        return len(self._xl.sheet_names())

    def sheet_names(self):
        return self._xl.sheet_names()

    def set_sheet(self, sheet):
        if isinstance(sheet, int):
            if sheet >= len(self._xl.sheet_names()) or sheet < 0:
                raise IndexError('Workbook has no sheet for index {}'.format(
                    sheet))
            self._sheet = self._xl.sheet_by_index(sheet)
        elif isinstance(sheet, basestring):
            if sheet not in self._xl.sheet_names():
                raise ValueError('No such sheet name: {}'.format(sheet))
            self._sheet = self._xl.sheet_by_index(sheet)
        else:
            raise ValueError('sheet argument must be a string or integer')
        self._heads = convert_xl_row(self._xl, self._sheet.name, 0)
        uniqheads = set(self._heads)
        if None in uniqheads:
            self._sheet = None
            raise ValueError('Columns with no or blank header in header row')
        if len(uniqheads) < len(self._heads):
            self._sheet = None
            raise ValueError('Non unique values in the header row')

    def __iter__(self):
        if not self._sheet:
            raise StopIteration('No sheet set')
        s = self._sheet
        for i in xrange(1, s.nrows):
            self._rownum = i + 1
            r = convert_xl_row(self._xl, self._sheet.name, i)
            ret = {n: v for n, v in zip(self._heads, r)}
            yield ret

    def sheet_headers(self):
        if not self._sheet:
            return None
        return self._heads


def convert_xl_row(xlrdbook, xlrdsheetname, rownum):
    '''Converts an xlrd excel worksheet row into a standard python format.
    Empty or blank cells -> None
    Number or text -> float or string
    Boolean -> True or False
    Date -> date string as rendered by datetime.datetime
    Raises ValueError if a cell has an error.'''
    xlrdsheet = xlrdbook.sheet_by_name(xlrdsheetname)
    ret = []
    for i, cell in enumerate(xlrdsheet.row(rownum)):
        if (cell.ctype == _xlrd.XL_CELL_EMPTY or
            cell.ctype == _xlrd.XL_CELL_BLANK):
            ret.append(None)
        elif (cell.ctype == _xlrd.XL_CELL_NUMBER or
              cell.ctype == _xlrd.XL_CELL_TEXT):
            ret.append(cell.value)
        elif cell.ctype == _xlrd.XL_CELL_BOOLEAN:
            ret.append(bool(cell.value))
        elif cell.ctype == _xlrd.XL_CELL_DATE:
            dt = _xlrd.xldate_as_tuple(cell.value, xlrdbook.datemode)
            d = str(_datetime.datetime(*dt))
            ret.append(d)
        elif cell.ctype == _xlrd.XL_CELL_ERROR:
            raise ValueError(
                ' '.join(['Cell', _xlrd.cellname(rownum, i), 'in sheet',
                xlrdsheet.name, 'has an error']))
        else:
            raise ValueError('Unknown cell type')  # error in xlrd
    return ret
