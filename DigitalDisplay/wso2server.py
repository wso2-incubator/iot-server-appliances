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
import logging
from logging.handlers import RotatingFileHandler

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
formatter = logging.Formatter(
    '%(asctime)s - %(levelname)s: %(name)s.%(funcName)s()@%(lineno)d: %(message)s')
f_handler.setFormatter(formatter)
c_handler.setFormatter(formatter)

# add the handlers to the logger
LOGGER.addHandler(f_handler)
LOGGER.addHandler(c_handler)

# color logger streaming

# ---------------------------------------------------------------------------
#   Importing other modules and defining global variables
# ---------------------------------------------------------------------------
from modules.resource_types import *
from modules.update_listener import *
from modules.config.config_reader import *
from modules.ddbrowser import *
from modules.sequence_runner import *
from modules.httpserver import *
import subprocess

cont_conf = {}
kernel_conf = {}

# ---------------------------------------------------------------------------
#   Reading configurations
# ---------------------------------------------------------------------------
def read_kernel_config():
    global kernel_conf
    kernel_conf = ConfigReader.read_config(
        kernel_utils.base_path + '/conf/digital_display_kernelrunner.xml')


def read_content_config():
    global cont_conf
    cont_conf = ConfigReader.read_config(
        kernel_utils.content_path + '/conf/digital_display_content.xml')


# ---------------------------------------------------------------------------
#   Running the sequence
# ---------------------------------------------------------------------------
def get_sequence_runner():
    # get all defined resources for the sequence
    try:
        resources_conf = cont_conf['DigitalDisplay']['Content']['DisplaySequence']['Resource']
    except KeyError:
        # we can't continue, no resources found!
        raise ConfigError("No 'Resource' elements found under the 'DisplaySequence'...!")

    # get browser configurations
    try:
        web_browser_conf = kernel_conf['DigitalDisplay']['KernelRunner']['WebBrowser']
    except KeyError:
        # can't continue no browser conf!
        raise ConfigError("No 'WebBrowser' elements found under the 'KernelRunner'...!")

    # open up the browser
    web_browser = WebBrowser(web_browser_conf)
    web_browser.start()

    # execute our sequence!
    sequence_runner = SequenceRunner(resources_conf, web_browser)
    return sequence_runner


# ---------------------------------------------------------------------------
#   HTTP Web Server
# ---------------------------------------------------------------------------
def _get_sid():
    setsid = getattr(os, 'setsid', None)
    if not setsid:
        setsid = getattr(os, 'setpgrp', None)
    return setsid


def start_server(_path, _port=8000):
    httpd = subprocess.Popen(
        ["python", os.path.join(kernel_utils.modules_path, "httpserver.py"), _path, str(_port)],
        close_fds=True, preexec_fn=_get_sid())
    return httpd


# ---------------------------------------------------------------------------
#   Check for content/ kernel updates
# ---------------------------------------------------------------------------
def safe_exit_handler():
    """
    Callback method needed to run just before update take place.
    """
    LOGGER.debug("Exiting Gracefully...")

    # if we are in a middle of a sequence phase; just stop it immediately
    current_phase_resource = SequenceRunner.current_phase_resource
    if current_phase_resource:
        current_phase_resource.stop({'delay': 0})


def poll_update(exit_handler):
    # first, get all handlers defined
    try:
        handlers = kernel_conf['DigitalDisplay']['KernelRunner']['VCSHandlers']['Handler']
    except KeyError:
        # damn!, no handlers defined, raise error
        LOGGER.error("No VCSHandler found...!")
        raise

    # initiate update listener with update policy
    try:
        update_config = kernel_conf['DigitalDisplay']['KernelRunner']['UpdatePolicy']
        lock = threading.Lock()
        UpdateListener.update(handlers, update_config, lock, exit_handler)
    except KeyError:
        # okay!, no update policy defined, ignore the error
        LOGGER.debug("No update policy found, skipping update...!")
        pass


# ---------------------------------------------------------------------------
#   Main method
# ---------------------------------------------------------------------------
def main():
    global sequence_runner
    LOGGER.info("Server Starting...")

    # read configurations
    read_kernel_config()
    read_content_config()

    # starting server
    start_server(kernel_utils.web_content_path)

    # start polling repositories
    poll_update(safe_exit_handler)

    # start sequence
    sequence_runner = get_sequence_runner()
    sequence_runner.run_sequence()
    time.sleep(10)  # startup delay for pulling updates


if __name__ == '__main__':
    main()
