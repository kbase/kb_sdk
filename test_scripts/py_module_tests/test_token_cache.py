'''
Created on Aug 2, 2016

@author: gaprice@lbl.gov
'''
import unittest

from authclient import TokenCache  # @UnresolvedImport
import time


class TestTokenCache(unittest.TestCase):

    def test_set_and_get(self):
        tc = TokenCache()
        tc.add_valid_token('foo', 'bar')
        self.assertEqual('bar', tc.get_user('foo'))
        tc.add_valid_token('baz', 'bat')
        self.assertEqual('bat', tc.get_user('baz'))
        self.assertEqual(None, tc.get_user('womp'))

    def test_fail_set(self):
        self.fail_set(None, 'foo', 'Must supply token')
        self.fail_set('foo', None, 'Must supply user')

    def test_fast_expire(self):
        tc = TokenCache()
        tc._MAX_TIME_SEC = 1
        tc.add_valid_token('foo', 'bar')
        self.assertEqual('bar', tc.get_user('foo'))
        time.sleep(2)
        self.assertEqual(None, tc.get_user('foo'))

    def test_drop_tokens(self):
        sz = 4
        tc = TokenCache(sz)
        for x in xrange(1, sz + 1):
            tc.add_valid_token(str(x), x)
        for x in xrange(1, sz + 1):
            self.assertEqual(x, tc.get_user(str(x)))

        # shouldn't dump the cache
        tc.add_valid_token('2', 2)
        for x in xrange(1, sz + 1):
            self.assertEqual(x, tc.get_user(str(x)))

        # should dump the cache
        tc.add_valid_token('5', 5)
        for x in [1, 3, 4]:
            self.assertEqual(None, tc.get_user(str(x)))
        for x in [2, 5]:
            self.assertEqual(x, tc.get_user(str(x)))

    def fail_set(self, token, user, error):
        with self.assertRaises(ValueError) as context:
            TokenCache().add_valid_token(token, user)
        self.assertEqual(error, str(context.exception.message))
