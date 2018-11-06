'''
Created on Aug 1, 2016

A very basic KBase auth client for the Python server.

@author: gaprice@lbl.gov
'''
import time as _time
import requests as _requests
import threading as _threading
import hashlib


class TokenCache(object):
    ''' A basic cache for tokens. '''

    _MAX_TIME_SEC = 5 * 60  # 5 min

    _lock = _threading.RLock()

    def __init__(self, maxsize=2000):
        self._cache = {}
        self._maxsize = maxsize
        self._halfmax = maxsize / 2  # int division to round down

    def get_user(self, token):
        token = hashlib.sha256(token.encode('utf-8')).hexdigest()
        with self._lock:
            usertime = self._cache.get(token)
        if not usertime:
            return None

        user, intime = usertime
        if _time.time() - intime > self._MAX_TIME_SEC:
            return None
        return user

    def add_valid_token(self, token, user):
        if not token:
            raise ValueError('Must supply token')
        if not user:
            raise ValueError('Must supply user')
        token = hashlib.sha256(token.encode('utf-8')).hexdigest()
        with self._lock:
            self._cache[token] = [user, _time.time()]
            if len(self._cache) > self._maxsize:
                sorted_items = sorted(
                    list(self._cache.items()),
                    key=(lambda v: v[1][1])
                )
                for i, (t, _) in enumerate(sorted_items):
                    if i <= self._halfmax:
                        del self._cache[t]
                    else:
                        break


class KBaseAuth(object):
    '''
    A very basic KBase auth client for the Python server.
    '''

    _LOGIN_URL = 'https://kbase.us/services/auth/api/legacy/KBase/Sessions/Login'

    def __init__(self, auth_url=None):
        '''
        Constructor
        '''
        self._authurl = auth_url
        if not self._authurl:
            self._authurl = self._LOGIN_URL
        self._cache = TokenCache()

    def get_user(self, token):
        if not token:
            raise ValueError('Must supply token')
        user = self._cache.get_user(token)
        if user:
            return user

        d = {'token': token, 'fields': 'user_id'}
        ret = _requests.post(self._authurl, data=d)
        if not ret.ok:
            try:
                err = ret.json()
            except Exception as e:
                ret.raise_for_status()
            raise ValueError('Error connecting to auth service: {} {}\n{}'
                             .format(ret.status_code, ret.reason,
                                     err['error']['message']))

        user = ret.json()['user_id']
        self._cache.add_valid_token(token, user)
        return user
