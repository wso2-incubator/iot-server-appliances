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
import json
import logging
import time

import kernel_utils as kernel_utils


LOGGER = logging.getLogger('wso2server.ddbrowser')


class WebBrowserError(Exception):
    """WebBrowser Exception class"""
    pass


class BaseBrowser(object):
    """Parent class for all browsers. Do not use directly."""

    name = "undefined"
    args = ['%s']
    attemt=1

    def __init__(self, path_="", exists=False):
        self.path = path_

    @staticmethod
    def _get_sid():
        setsid = getattr(os, 'setsid', None)
        if not setsid:
            setsid = getattr(os, 'setpgrp', None)
        return setsid

    def open(self, url_):
        # if possible, put browser in separate process group, so
        # keyboard interrupts don't affect browser as well as Python

        LOGGER.info("open_base_browser")
        data = {"agent":"displayagent","version":"1.0.0","path":url_}
        with open(kernel_utils.web_content_path + os.sep + "_system" + os.sep + "current_resource.json", 'w') as outfile:
            outfile.seek(0)
            json.dump(data, outfile)
            outfile.truncate()


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


class DefaultBrowser(BaseBrowser):
    name = "default"
    args = ["%s"]

    def __init__(self, path_="", exists=False):
        BaseBrowser.__init__(self, path_)
        LOGGER.warning("Webbrowser")
        subprocess.Popen(path_ + " http://localhost:8000/_system/", shell=True)


class WebBrowser(object):
    name = None
    path = None
    port = None

    def __init__(self, webbrowser_conf):
        if webbrowser_conf is None:
            raise WebBrowserError("Initialization error: webbrowser_conf cannot be null!")
        try:
            self.name = webbrowser_conf['Name']
            self.path = webbrowser_conf['Path']
            self.port = webbrowser_conf['Port']
        except KeyError:
            raise WebBrowserError("Initialization error: Name, Path or Port is not configured!")

        # get all subclasses implementing BaseBrowser
        self.browser_types = globals()['BaseBrowser'].__subclasses__()
        self.ddwebbrowser = None

    def get_browser(self, type_):
        """
        This method returns implementing class for specific browserTypes.
        @return browserType
        @throws NotImplementedError when browserType is Unknown
        """
        try:
            browser_type = [cls for cls in self.browser_types if cls.__dict__['name'] == type_][0]
            return browser_type
        except IndexError:
            return BaseBrowser

    def kill(self):
        self.ddwebbrowser.kill()

    def start(self):
        browser_class = self.get_browser(self.name)
        exists_ = kernel_utils.check_process(self.path)
        # initialize new browser
        self.ddwebbrowser = browser_class(self.path, exists=exists_)
        return self.ddwebbrowser

    def open(self, url):
        self.ddwebbrowser.open(url)