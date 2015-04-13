'''
Created on Nov 11, 2012

@author: gaprice@lbl.gov
'''


def chunkiter(iterable, size):
    """Iterates over an iterable in chunks of size size. Returns an iterator
  that in turn returns iterators over the iterable that each iterate through
  size objects in the iterable.
  Note that since the inner and outer loops are pulling values from the same
  iterator, continue and break don't necessarily behave exactly as one would
  expect. In the outer loop of the iteration, continue effectively does
  nothing, but break works normally. In the inner loop, break has no real
  effect but continue works normally. For the latter issue, wrapping the inner
  iterator in a tuple will cause break to skip the remaining items in the
  iterator. Alternatively, one can set a flag and exhaust the inner iterator.
  """
    def inneriter(first, iterator, size):
        yield first
        for _ in xrange(size - 1):
            yield iterator.next()
    it = iter(iterable)
    while True:
        yield inneriter(it.next(), it, size)
