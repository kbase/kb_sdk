'''
Created on Sep 6, 2012

@author: gaprice@lbl.gov

Some class related tools
'''


class Immutable(object):
    '''
  Inheriting from Immutable disables modification of attributes except by
  a non-standard method, preventing accidental modification in code.'''

    def __setattr__(self, name, value):
        raise SetAttrOnImmutableObjectError(
            "This object is immutable for good reason- knock it off")

    def __delattr__(self, name, value):
        raise DelAttrOnImmutableObjectError(
            "This object is immutable for good reason- knock it off")

    def _setattrbyobj(self, name, value):
        object.__setattr__(self, name, value)


class SetAttrOnImmutableObjectError(Exception):
    pass


class DelAttrOnImmutableObjectError(Exception):
    pass


class abstractclassmethod(classmethod):
    """
    A decorator indicating abstract classmethods.

    Similar to abstractmethod.

    Usage:

        class C(metaclass=ABCMeta):
            @abstractclassmethod
            def my_abstract_classmethod(cls, ...):
                ...

    'abstractclassmethod' is deprecated. Use 'classmethod' with
    'abstractmethod' instead.
    """
# hacked version from py3. Note can't use the classmethod and abstract
# methods together in py2.

    __isabstractmethod__ = True

    def __init__(self, callable_):
        callable_.__isabstractmethod__ = True
        super(abstractclassmethod, self).__init__(callable_)
