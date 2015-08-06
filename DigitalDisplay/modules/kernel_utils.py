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
import re
import os
import logging

LOGGER = logging.getLogger('wso2server.kernel_utils')

# ---------------------------------------------------------------------------
#   Defining folder paths
# ---------------------------------------------------------------------------
__TEMP_FOLDER = "tmp"
__RESOURCES_FOLDER = "resources"
__WWW_FOLDER = "www"
__SCRIPTS_FOLDER = "scripts"

# change os current path to script base folder
os.chdir(os.path.normpath(os.path.dirname(os.path.abspath(__file__))))

# define folder paths
base_path = os.path.normpath(os.path.dirname(os.path.abspath(__file__)) + os.sep + os.pardir)
modules_path = os.path.dirname(os.path.abspath(__file__))
temp_path = os.path.normpath(os.path.dirname(os.path.abspath(__file__)) + os.sep + os.pardir + os.sep + __TEMP_FOLDER)
content_path = os.path.normpath(os.path.dirname(os.path.abspath(__file__)) + os.sep + os.pardir + os.sep + __RESOURCES_FOLDER)
web_content_path = os.path.normpath(content_path + os.sep + __WWW_FOLDER)
scripts_path = os.path.normpath(os.path.dirname(os.path.abspath(__file__)) + os.sep + os.pardir + os.sep + __SCRIPTS_FOLDER)

# ---------------------------------------------------------------------------
#   Utility methods
# ---------------------------------------------------------------------------
def get_seconds(str_):
    """
    Converts human readable time strings into seconds
    :param str_: time string(eg. 1d 2h 3m 4s)
    :return: seconds
    """
    sec = 0
    splitted_list = re.findall('[0-9]*[dhms]\\b', str_)
    for stime in splitted_list:
        if stime.endswith("d"):
            sec += int(stime[:-1]) * 24 * 60 * 60
        elif stime.endswith("h"):
            sec += int(stime[:-1]) * 60 * 60
        elif stime.endswith("m"):
            sec += int(stime[:-1]) * 60
        elif stime.endswith("s"):
            sec += int(stime[:-1])
    return sec


def check_process(process):
    """
    Check if process exists
    :param process: process name
    :return: True if exists
    """
    import re
    import subprocess

    process_exists = False
    s = subprocess.Popen(["ps", "ax"], stdout=subprocess.PIPE)
    for x in s.stdout:
        if re.search(process, x):
            process_exists = True

    return process_exists


