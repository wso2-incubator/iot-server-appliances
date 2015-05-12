"""
/**
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
**/
"""
import subprocess
import os
import sys
import logging
import time

import kernel_utils as kernel_utils


LOGGER = logging.getLogger('wso2server.ddbrowser')


class BaseBrowser(object):
    """Parent class for all browsers. Do not use directly."""

    name = "undefined"
    args = ['%s']

    def __init__(self, path_="", exists=False):
        self.path = path_

    def _get_sid(self):
        setsid = getattr(os, 'setsid', None)
        if not setsid:
            setsid = getattr(os, 'setpgrp', None)
        return setsid

    def open(self, url_):
        # if possible, put browser in separate process group, so
        # keyboard interrupts don't affect browser as well as Python
        try:
            pargs = [self.path] + [arg.replace("%s", url_) for arg in self.args]
            LOGGER.debug(" ".join(pargs))
            p = subprocess.Popen(" ".join(pargs), close_fds=True, preexec_fn=self._get_sid(), shell=True)

        except OSError, e:
            if e.errno == 2:
                LOGGER.warning("Webbrowser `" + self.name + "` at `" + self.path + "` not found...!")
            else:
                raise
            sys.exit(0)
        return p


class MidoriBrowser(BaseBrowser):
    name = "midori"
    args = ["%s"]
    # args = ["-a", "%s"]

    def __init__(self, path_="", exists=False):
        BaseBrowser.__init__(self, path_)
        if exists:
            p = subprocess.Popen("killall midori", shell=True)
            p.wait()
            time.sleep(2)
        LOGGER.warning("Webbrowser -TabCloseOther")
        subprocess.Popen(path_ + ' -e TabCloseOther', shell=True)
        time.sleep(10)
        LOGGER.warning("Webbrowser -Fullscreen")
        subprocess.Popen(path_ + ' -e Fullscreen', shell=True)
        time.sleep(2)


class ChromeBrowser(BaseBrowser):
    name = "chrome"
    args = ["--app", "%s"]


"""
This method returns implementing class for specific browserTypes.
@return browserType
@throws NotImplementedError when browserType is Unknown
"""
browser_types = vars()['BaseBrowser'].__subclasses__()  # get all subclasses implementing BaseBrowser


def get_browser_type(type_):
    try:
        browser_type = [cls for cls in browser_types if cls.__dict__['name'] == type_][0]
    except IndexError:
        return BaseBrowser
    return browser_type


ddwebbrowser = None


def kill():
    ddwebbrowser.kill()


def start(webbrowser_conf):
    global ddwebbrowser
    browser_class = get_browser_type(webbrowser_conf['Name'])
    exists_ = kernel_utils.check_process(webbrowser_conf['Path'])
    ddwebbrowser = browser_class(webbrowser_conf['Path'], exists=exists_)
    return ddwebbrowser
