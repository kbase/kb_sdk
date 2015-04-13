'''
Created on Sep 15, 2012

@author: gaprice@lbl.gov
'''

from collections import defaultdict as _defaultdict


class AutoVivifingDict(dict):
    """Implementation of perl's autovivification feature."""
# see http://stackoverflow.com/questions/651794/whats-the-best-way-to-
# initialize-a-dict-of-dicts-in-python

    def __getitem__(self, item):
        try:
            return dict.__getitem__(self, item)
        except KeyError:
            value = self[item] = type(self)()
            return value


class DictListWithSortedIterator(object):
    ''' Implements a dict-like object, the values of which are lists of
  objects. The iterator returned from the __iter__ method traverses the lists
  in order of 1) the sorted list of keys and 2) the order the items were added
  to the list. Once iteration is started, the DLWSI cannot be modified until
  the iterator is exhausted or the discard() method is called on the
  iterator.'''

    def __init__(self):
        self._store = _defaultdict(list)
        self._itercount = 0
        self._len = 0

    def __setitem__(self, key, value):
        '''Add value to the end of the list stored at key'''
        self._check_iter_ok()
        self._store[key].append(value)
        self._len += 1

    def __delitem__(self, key):
        '''Delete the list stored at key.'''
        self._check_iter_ok()
        if key in self._store:
            l = len(self._store[key])
            del self._store[key]
            self._len -= l

    def __getitem__(self, key):
        '''Returns the list stored at key as a tuple.'''
        if self._store[key]:
            return tuple(self._store[key])
        else:
            raise KeyError(str(key))

    def get(self, key, default=None):
        '''Returns the list stored at key as a tuple. The default argument
    specifies the object to return if key does not exist (default None).'''
        if self._store[key]:
            return tuple(self._store[key])
        return default

    def keys(self):
        '''Returns an unsorted list of keys.'''
        return self._store.keys()

    def __len__(self):
        return self._len

    def clear(self):
        '''Removes all keys and values from the DLWSI.'''
        self._check_iter_ok()
        self._store = _defaultdict(list)
        self._len = 0

    def merge(self, dictlist):
        '''Adds all key value pairs of the passed in DLWSI to this DLWSI. Any
    keys in this DLWSI that have matching names to keys in the passed in DLWSI
    will be overwritten.'''
        self._check_iter_ok()
        for k, v in dictlist:
            self.__setitem__(k, v)

    def _check_iter_ok(self):
        if self._itercount:
            raise RuntimeError('Attempt to modify while iterating')

    def __iter__(self):
        '''Returns an iterator over this DLWSI. The iterator proceeds through
    each list in the order of the sorted keys and returns a key / list item
    pair for each next call. Thus if a particular key has 3 list items that
    key will be returned 3 times in succession, once with each list item.
    The DLWSI cannot be modified while iterating. To allow modification without
    exhausting the iterator call the discard() method on the iterator.'''
        if not self._itercount:
            self._sortedKeys = sorted(self._store.keys())
        self._itercount += 1
        return self._ObjIter(self)

    class _ObjIter(object):

        def __init__(self, objStore):
            self._ostore = objStore
            self._dictindex = 0
            self._listindex = -1
            self._notexhausted = True

        def next(self):
            if self._notexhausted and self._has_next():
                self._advance_index()
                return self._get_current_key_val_tuple()
            else:
                self._dec_iter_count()
                raise StopIteration

        def discard(self):
            self._dec_iter_count()

        def _dec_iter_count(self):
            if self._notexhausted:
                self._ostore._itercount -= 1
            self._notexhausted = False

        def _has_next(self):
            if len(self._ostore._store) == 0:
                return False
            dictI = self._dictindex
            if self._listindex + 1 >= len(self._get_list(
                                          self._get_sorted_key(dictI))):
                dictI += 1
            return dictI < len(self._ostore._sortedKeys)

        def _advance_index(self):
            if self._listindex + 1 >= len(self._get_list(self._get_sorted_key(
                                                         self._dictindex))):
                self._dictindex += 1
                self._listindex = 0
            else:
                self._listindex += 1

        def _get_current_key_val_tuple(self):
            key = self._get_sorted_key(self._dictindex)
            return key, self._get_list(key)[self._listindex]

        def _get_sorted_key(self, index):
            return self._ostore._sortedKeys[index]

        def _get_list(self, key):
            return self._ostore._store[key]

        def __next__(self):
            return self.next()
