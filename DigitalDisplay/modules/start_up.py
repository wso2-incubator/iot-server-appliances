#!/usr/bin/env python
# encoding: utf-8

# Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
#
# WSO2 Inc. licenses this file to you under the Apache License,
# Version 2.0 (the "License"); you may not use this file except
# in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.


"""
WSO2 Display Agent server implementation for python.

Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.

To use, simply run as 'python wso2sever.py' or './wso2server.py'!
"""

# ---------------------------------------------------------------------------
#   Configuration parent logger
# ---------------------------------------------------------------------------
from logging.handlers import RotatingFileHandler
from update_listener import *
from config.config_reader import *
from ddbrowser import *
from sequence_runner import *
from httpserver import *
import subprocess


class ContentUtilityClass(object):
    cont_conf = {}
    kernel_conf = {}
    httpd = None
    _instance = None
    web_browser = None
    resources_conf = None
    web_browser_conf = None
    player_conf = None

    # for implement singleton pattern
    def __new__(cls, *args, **kwargs):
        if not cls._instance:
            cls._instance = super(ContentUtilityClass, cls).__new__(cls, *args, **kwargs)
        return cls._instance

    def create_logger(self):

        # create logger
        LOGGER = logging.getLogger('wso2server')
        LOGGER.setLevel(logging.DEBUG)

        # create file handler
        f_handler = RotatingFileHandler('wso2server.log', mode='a', maxBytes=1 * 1024 * 1024, backupCount=0,
                                        encoding=None,
                                        delay=0)
        f_handler.setLevel(logging.ERROR)

        # create console handler with
        c_handler = logging.StreamHandler()
        c_handler.setLevel(logging.DEBUG)

        # create formatter and add it to the handlers
        formatter = logging.Formatter('%(asctime)s - %(levelname)s: %(name)s.%(funcName)s()@%(lineno)d: %(message)s')
        f_handler.setFormatter(formatter)
        c_handler.setFormatter(formatter)

        # add the handlers to the logger
        LOGGER.addHandler(f_handler)
        LOGGER.addHandler(c_handler)

        # color logger streaming

    # ---------------------------------------------------------------------------
    #   Reading configurations
    # ---------------------------------------------------------------------------
    def read_kernel_config(self):
        self.kernel_conf = ConfigReader.read_config(
            kernel_utils.base_path + '/conf/digital_display_kernelrunner.xml')

    def read_content_config(self):
        self.cont_conf = ConfigReader.read_config(
            kernel_utils.content_path + '/conf/digital_display_content.xml')

    # ---------------------------------------------------------------------------
    #   Running the sequence
    # ---------------------------------------------------------------------------
    def get_sequence_runner(self):
        # get all defined resources for the sequence
        try:
            self.set_resources_conf()
        except KeyError:
            # we can't continue, no resources found!
            raise ConfigError("No 'Resource' elements found under the 'DisplaySequence'...!")

        # get browser configurations
        try:
            self.web_browser_conf = self.kernel_conf['DigitalDisplay']['KernelRunner']['WebBrowser']
        except KeyError:
            # can't continue no browser conf!
            raise ConfigError("No 'WebBrowser' elements found under the 'KernelRunner'...!")

        try:
            self.player_conf = self.kernel_conf['DigitalDisplay']['KernelRunner']['Player']
        except KeyError:
            # can't continue no browser conf!
            raise ConfigError("No 'Player' elements found under the 'KernelRunner'...!")

        # open up the browser
        self.open_browser()

        # execute our sequence!
        sequence_runner = SequenceRunner()
        sequence_runner.set_all_conf(self.resources_conf, self.web_browser, self.player_conf)
        return sequence_runner

    # not sure (create WebBrowser object when this method call)
    def open_browser(self):
        self.web_browser = WebBrowser(self.web_browser_conf)
        self.web_browser.start()

    def close_browser(self):
        self.web_browser.close()

    def set_resources_conf(self):
        self.read_content_config()
        self.resources_conf = self.cont_conf['DigitalDisplay']['Content']['DisplaySequence']['Resource']

    def get_resources_conf(self):
        return self.resources_conf

    # ---------------------------------------------------------------------------
    #   HTTP Web Server
    # ---------------------------------------------------------------------------
    def _get_sid(self):
        setsid = getattr(os, 'setsid', None)
        if not setsid:
            setsid = getattr(os, 'setpgrp', None)
        return setsid

    def start_server(self, _path, _port=8000):
        global httpd
        self.httpd = subprocess.Popen(
            ["python", os.path.join(kernel_utils.modules_path, "httpserver.py"), _path, str(_port)],
            close_fds=True, preexec_fn=self._get_sid())
        return self.httpd

    def terminate_server(self):
        try:
            LOGGER.info("Terminate Server")
            self.httpd.kill()
        except Exception as e:
            LOGGER.error("Exception "+str(e))

    # ---------------------------------------------------------------------------
    #   Check for content/ kernel updates
    # ---------------------------------------------------------------------------
    def safe_exit_handler(self):
        """
        Callback method needed to run just before update take place.
        """
        LOGGER.debug("Exiting Gracefully...")

        # if we are in a middle of a sequence phase; just stop it immediately
        current_phase_resource = SequenceRunner.current_phase_resource
        if current_phase_resource:
            current_phase_resource.stop({'delay': 0})

    def poll_update(self, exit_handler):
        # first, get all handlers defined
        try:
            handlers = self.kernel_conf['DigitalDisplay']['KernelRunner']['VCSHandlers']['Handler']
        except KeyError:
            # damn!, no handlers defined, raise error
            LOGGER.error("No VCSHandler found...!")
            raise

        # initiate update listener with update policy
        try:
            update_config = self.kernel_conf['DigitalDisplay']['KernelRunner']['UpdatePolicy']
            lock = threading.Lock()
            UpdateListener.update(handlers, update_config, lock, exit_handler)
        except KeyError:
            # okay!, no update policy defined, ignore the error
            LOGGER.debug("No update policy found, skipping update...!")
            pass