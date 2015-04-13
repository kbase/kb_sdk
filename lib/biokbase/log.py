"""
NAME
       log

DESCRIPTION
       A library for sending logging messages to syslog.

METHODS
       log(string subsystem, hashref constraints): Initializes log. You
           should call this at the beginning of your program. Constraints are
           optional.

       log_message(int level, string message): sends log message to syslog.

       *         level: (0-9) The logging level for this message is compared to
                    the logging level that has been set in log.  If it is <=
                    the set logging level, the message will be sent to syslog,
                    otherwise it will be ignored.  Logging level is set to 6
                    if control API cannot be reached and the user does
                    not set the log level. Log level can also be entered as
                    string (e.g. 'DEBUG')

       *          message: This is the log message.

       get_log_level(): Returns the current log level as an integer.

       set_log_level(integer level) : Sets the log level. Only use this if you
           wish to override the log levels that are defined by the control API.
           Can also be entered as string (e.g. 'DEBUG')

       *          level : priority

       *          0 : EMERG - system is unusable

       *          1 : ALERT - component must be fixed immediately

       *          2 : CRIT - secondary component must be fixed immediately

       *          3 : ERR - non-urgent failure

       *          4 : WARNING - warning that an error will occur if no action
                                is taken

       *          5 : NOTICE - unusual but safe conditions

       *          6 : INFO - normal operational messages

       *          7 : DEBUG - lowest level of debug

       *          8 : DEBUG2 - second level of debug

       *          9 : DEBUG3 - highest level of debug

       set_log_msg_check_count(integer count): used to set the number the
           messages that log will log before querying the control API for the
           log level (default is 100 messages).

       set_log_msg_check_interval(integer seconds): used to set the interval,
           in seconds, that will be allowed to pass before log will query the
           control API for the log level (default is 300 seconds).

       update_api_log_level() : Checks the control API for the currently set
           log level.

       use_api_log_level() : Removes the user-defined log level and tells log
           to use the control API-defined log level.
"""

import json as _json
import urllib2 as _urllib2
import syslog as _syslog
import platform as _platform
import inspect as _inspect
import os as _os
import getpass as _getpass
import warnings as _warnings
from ConfigParser import ConfigParser as _ConfigParser
import time

MLOG_ENV_FILE = 'MLOG_CONFIG_FILE'
_GLOBAL = 'global'
MLOG_LOG_LEVEL = 'mlog_log_level'
MLOG_API_URL = 'mlog_api_url'
MLOG_LOG_FILE = 'mlog_log_file'

DEFAULT_LOG_LEVEL = 6
#MSG_CHECK_COUNT = 100
#MSG_CHECK_INTERVAL = 300  # 300s = 5min
MSG_FACILITY = _syslog.LOG_LOCAL1
EMERG_FACILITY = _syslog.LOG_LOCAL0

EMERG = 0
ALERT = 1
CRIT = 2
ERR = 3
WARNING = 4
NOTICE = 5
INFO = 6
DEBUG = 7
DEBUG2 = 8
DEBUG3 = 9
_MLOG_TEXT_TO_LEVEL = {'EMERG': EMERG,
                      'ALERT': ALERT,
                      'CRIT': CRIT,
                      'ERR': ERR,
                      'WARNING': WARNING,
                      'NOTICE': NOTICE,
                      'INFO': INFO,
                      'DEBUG': DEBUG,
                      'DEBUG2': DEBUG2,
                      'DEBUG3': DEBUG3,
                      }
_MLOG_TO_SYSLOG = [_syslog.LOG_EMERG, _syslog.LOG_ALERT, _syslog.LOG_CRIT,
                 _syslog.LOG_ERR, _syslog.LOG_WARNING, _syslog.LOG_NOTICE,
                 _syslog.LOG_INFO, _syslog.LOG_DEBUG, _syslog.LOG_DEBUG,
                 _syslog.LOG_DEBUG]
#ALLOWED_LOG_LEVELS = set(_MLOG_TEXT_TO_LEVEL.values())
_MLOG_LEVEL_TO_TEXT = {}
for k, v in _MLOG_TEXT_TO_LEVEL.iteritems():
    _MLOG_LEVEL_TO_TEXT[v] = k
LOG_LEVEL_MIN = min(_MLOG_LEVEL_TO_TEXT.keys())
LOG_LEVEL_MAX = max(_MLOG_LEVEL_TO_TEXT.keys())
del k, v


class log(object):
    """
    This class contains the methods necessary for sending log messages.
    """

    def __init__(self, subsystem, constraints=None, config=None, logfile=None,
                 ip_address=False, authuser=False, module=False,
                 method=False, call_id=False, changecallback=None):
        if not subsystem:
            raise ValueError("Subsystem must be supplied")

        self.user = _getpass.getuser()
        self.parentfile = _os.path.abspath(_inspect.getfile(
            _inspect.stack()[1][0]))
        self.ip_address = ip_address
        self.authuser = authuser
        self.module = module
        self.method = method
        self.call_id = call_id
        noop = lambda: None
        self._callback = changecallback or noop
        self._subsystem = str(subsystem)
        self._mlog_config_file = config
        if not self._mlog_config_file:
            self._mlog_config_file = _os.environ.get(MLOG_ENV_FILE, None)
        if self._mlog_config_file:
            self._mlog_config_file = str(self._mlog_config_file)
        self._user_log_level = -1
        self._config_log_level = -1
        self._user_log_file = logfile
        self._config_log_file = None
        self._api_log_level = -1
        self._msgs_since_config_update = 0
        self._time_at_config_update = time.time()
        self.msg_count = 0
        self._recheck_api_msg = 100
        self._recheck_api_time = 300  # 5 mins
        self._log_constraints = {} if not constraints else constraints

        self._init = True
        self.update_config()
        self._init = False

    def _get_time_since_start(self):
        time_diff = time.time() - self._time_at_config_update
        return time_diff

    def get_log_level(self):
        if(self._user_log_level != -1):
            return self._user_log_level
        elif(self._config_log_level != -1):
            return self._config_log_level
        elif(self._api_log_level != -1):
            return self._api_log_level
        else:
            return DEFAULT_LOG_LEVEL

    def _get_config_items(self, cfg, section):
        cfgitems = {}
        if cfg.has_section(section):
            for k, v in cfg.items(section):
                cfgitems[k] = v
        return cfgitems

    def update_config(self):
        loglevel = self.get_log_level()
        logfile = self.get_log_file()

        self._api_log_level = -1
        self._msgs_since_config_update = 0
        self._time_at_config_update = time.time()

        # Retrieving the control API defined log level
        api_url = None
        if self._mlog_config_file and _os.path.isfile(self._mlog_config_file):
            cfg = _ConfigParser()
            cfg.read(self._mlog_config_file)
            cfgitems = self._get_config_items(cfg, _GLOBAL)
            cfgitems.update(self._get_config_items(cfg, self._subsystem))
            if MLOG_LOG_LEVEL in cfgitems:
                try:
                    self._config_log_level = int(cfgitems[MLOG_LOG_LEVEL])
                except:
                    _warnings.warn(
                        'Cannot parse log level {} from file {} to int'.format(
                            cfgitems[MLOG_LOG_LEVEL], self._mlog_config_file)
                        + '. Keeping current log level.')
            if MLOG_API_URL in cfgitems:
                api_url = cfgitems[MLOG_API_URL]
            if MLOG_LOG_FILE in cfgitems:
                self._config_log_file = cfgitems[MLOG_LOG_FILE]
        elif self._mlog_config_file:
            _warnings.warn('Cannot read config file ' + self._mlog_config_file)

        if (api_url):
            subsystem_api_url = api_url + "/" + self._subsystem
            try:
                data = _json.load(_urllib2.urlopen(subsystem_api_url,
                                                   timeout=5))
            except _urllib2.URLError, e:
                code_ = None
                if hasattr(e, 'code'):
                    code_ = ' ' + str(e.code)
                _warnings.warn(
                    'Could not connect to mlog api server at ' +
                    '{}:{} {}. Using default log level {}.'.format(
                    subsystem_api_url, code_, str(e.reason),
                    str(DEFAULT_LOG_LEVEL)))
            else:
                max_matching_level = -1
                for constraint_set in data['log_levels']:
                    level = constraint_set['level']
                    constraints = constraint_set['constraints']
                    if level <= max_matching_level:
                        continue

                    matches = 1
                    for constraint in constraints:
                        if constraint not in self._log_constraints:
                            matches = 0
                        elif (self._log_constraints[constraint] !=
                              constraints[constraint]):
                            matches = 0

                    if matches == 1:
                        max_matching_level = level

                self._api_log_level = max_matching_level
        if ((self.get_log_level() != loglevel or
             self.get_log_file() != logfile) and not self._init):
            self._callback()

    def _resolve_log_level(self, level):
        if(level in _MLOG_TEXT_TO_LEVEL):
            level = _MLOG_TEXT_TO_LEVEL[level]
        elif(level not in _MLOG_LEVEL_TO_TEXT):
            raise ValueError('Illegal log level')
        return level

    def set_log_level(self, level):
        self._user_log_level = self._resolve_log_level(level)
        self._callback()

    def get_log_file(self):
        if self._user_log_file:
            return self._user_log_file
        if self._config_log_file:
            return self._config_log_file
        return None

    def set_log_file(self, filename):
        self._user_log_file = filename
        self._callback()

    def set_log_msg_check_count(self, count):
        count = int(count)
        if count < 0:
            raise ValueError('Cannot check a negative number of messages')
        self._recheck_api_msg = count

    def set_log_msg_check_interval(self, interval):
        interval = int(interval)
        if interval < 0:
            raise ValueError('interval must be positive')
        self._recheck_api_time = interval

    def clear_user_log_level(self):
        self._user_log_level = -1
        self._callback()

    def _get_ident(self, level, user, parentfile, ip_address, authuser, module,
                   method, call_id):
        infos = [self._subsystem, _MLOG_LEVEL_TO_TEXT[level],
                 repr(time.time()), user, parentfile, str(_os.getpid())]
        if self.ip_address:
            infos.append(str(ip_address) if ip_address else '-')
        if self.authuser:
            infos.append(str(authuser) if authuser else '-')
        if self.module:
            infos.append(str(module) if module else '-')
        if self.method:
            infos.append(str(method) if method else '-')
        if self.call_id:
            infos.append(str(call_id) if call_id else '-')
        return "[" + "] [".join(infos) + "]"

    def _syslog(self, facility, level, ident, message):
        _syslog.openlog(ident, facility)
        if isinstance(message, basestring):
            _syslog.syslog(_MLOG_TO_SYSLOG[level], message)
        else:
            try:
                for m in message:
                    _syslog.syslog(_MLOG_TO_SYSLOG[level], m)
            except TypeError:
                _syslog.syslog(_MLOG_TO_SYSLOG[level], str(message))
        _syslog.closelog()

    def _log(self, ident, message):
        ident = ' '.join([str(time.strftime(
            "%Y-%m-%d %H:%M:%S", time.localtime())),
                        _platform.node(), ident + ': '])
        try:
            with open(self.get_log_file(), 'a') as log:
                if isinstance(message, basestring):
                    log.write(ident + message + '\n')
                else:
                    try:
                        for m in message:
                            log.write(ident + m + '\n')
                    except TypeError:
                        log.write(ident + str(message) + '\n')
        except Exception as e:
            err = 'Could not write to log file ' + str(self.get_log_file()) + \
                ': ' + str(e) + '.'
            _warnings.warn(err)

    def log_message(self, level, message, ip_address=None, authuser=None,
                    module=None, method=None, call_id=None):
#        message = str(message)
        level = self._resolve_log_level(level)

        self.msg_count += 1
        self._msgs_since_config_update += 1

        if(self._msgs_since_config_update >= self._recheck_api_msg
           or self._get_time_since_start() >= self._recheck_api_time):
            self.update_config()

        ident = self._get_ident(level, self.user, self.parentfile, ip_address,
                                authuser, module, method, call_id)
        # If this message is an emergency, send a copy to the emergency
        # facility first.
        if(level == 0):
            self._syslog(EMERG_FACILITY, level, ident, message)

        if(level <= self.get_log_level()):
            self._syslog(MSG_FACILITY, level, ident, message)
            if self.get_log_file():
                self._log(ident, message)

if __name__ == '__main__':
    pass
