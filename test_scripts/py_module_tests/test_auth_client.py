'''
Created on Aug 2, 2016

@author: gaprice@lbl.gov
'''
from __future__ import print_function

import unittest

try:
    from configparser import ConfigParser as _ConfigParser  # py 3
except ImportError:
    from ConfigParser import ConfigParser as _ConfigParser  # py 2

from authclient import KBaseAuth  # @UnresolvedImport
import os
import requests
from requests import ConnectionError


class TestAuth(unittest.TestCase):

    AUTHURL = 'test.auth-service-url'
    TOKEN1 = 'test.token'
    TOKEN2 = 'test.token2'

    CFG_SEC = 'kb_sdk_test'

    @classmethod
    def setUpClass(cls):
        configfile = os.path.abspath(os.path.dirname(
            os.path.abspath(__file__)) + '/../test.cfg')
        print('Loading test config from ' + configfile)
        cfg = _ConfigParser()
        cfg.read(configfile)
        authurl = cfg.get(cls.CFG_SEC, cls.AUTHURL)
        cls.token1 = cfg.get(cls.CFG_SEC, cls.TOKEN1)
        cls.token2 = cfg.get(cls.CFG_SEC, cls.TOKEN2)
        if not authurl:
            raise ValueError('Missing {} from test config'.format(cls.AUTHURL))
        if not cls.token1:
            raise ValueError('Missing {} from test config'.format(cls.TOKEN1))
        if not cls.token2:
            raise ValueError('Missing {} from test config'.format(cls.TOKEN2))
        print('Authorization url: ' + authurl)
        cls.user1 = cls.get_user(authurl, cls.token1)
        cls.user2 = cls.get_user(authurl, cls.token2)
        if cls.user1 == cls.user2:
            raise ValueError('{} and {} users are the same: {}'.format(
                cls.TOKEN1, cls.TOKEN2, cls.user1))
        cls.kba = KBaseAuth(authurl)

    @classmethod
    def get_user(cls, authurl, token):
        d = {'token': token, 'fields': 'user_id'}
        ret = requests.post(authurl, data=d)
        if ret.status_code != 200:
            print('Failed getting token: ' + str(ret.status_code))
            print(ret.text)
            raise Exception('Failed getting token: ' + str(ret.status_code))
        return ret.json()['user_id']

    def test_get_user(self):
        self.assertEqual(self.kba.get_user(self.token1), self.user1)
        self.assertEqual(self.kba.get_user(self.token2), self.user2)

        # test getting from cache
        self.assertEqual(self.kba.get_user(self.token1), self.user1)
        self.assertEqual(self.kba.get_user(self.token2), self.user2)

        # Only test the default url manually, since it runs against production.
        # kba2 = KBaseAuth()
        # self.assertEqual(kba2.get_user(self.token1), self.user1)
        # self.assertEqual(kba2.get_user(self.token2), self.user2)

    def test_bad_token(self):
        self.fail_get_user(None, 'Must supply token')
        self.fail_get_user(
            'bleah', 'Error connecting to auth service: 401 Unauthorized\n10020 Invalid token')
        self.fail_get_user(
            self.token1 + 'a',
            'Error connecting to auth service: 401 Unauthorized\n10020 Invalid token')

    def test_bad_url(self):
        kba2 = KBaseAuth('https://thisisasuperfakeurlihope.com')
        with self.assertRaises(ConnectionError) as context:
            kba2.get_user(self.token1)
        self.assertIn('not known', str(context.exception.args[0]))

    def fail_get_user(self, token, error):
        with self.assertRaises(ValueError) as context:
            self.kba.get_user(token)
        self.assertEqual(error, str(context.exception.args[0]))
