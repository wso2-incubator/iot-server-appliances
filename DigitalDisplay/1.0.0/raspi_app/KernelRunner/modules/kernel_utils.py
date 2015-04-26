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
import subprocess
import os
import logging
import colorstreamhandler

logging.basicConfig(level=logging.DEBUG)
LOGGER = logging.getLogger('org.wso2.iot.dd.raspi.kernel_utils')

#change os current path to script base folder
base_path = os.path.normpath(os.path.dirname(os.path.abspath(__file__))+ os.sep + os.pardir + os.sep + os.pardir)
kernel_path = os.path.normpath(os.path.dirname(os.path.abspath(__file__))+ os.sep + os.pardir)
modules_path = os.path.dirname(os.path.abspath(__file__))
temp_path = os.path.normpath(os.path.dirname(os.path.abspath(__file__))+ os.sep + os.pardir + os.sep + os.pardir + os.sep + "tmp")
content_path = os.path.normpath(os.path.dirname(os.path.abspath(__file__))+ os.sep + os.pardir + os.sep + os.pardir + os.sep + "Content")

def get_seconds(str_):
	#re.findall('[0-9]*[dhms]\\b',"1d 2h 3m 4s")
	sec = 0
	splitted_list = re.findall('[0-9]*[dhms]\\b', str_)
	for stime in splitted_list:
		if stime.endswith("d"):
			sec = sec + (int(stime[:-1])*24*60*60)
		elif stime.endswith("h"):
			sec = sec + (int(stime[:-1])*60*60)
		elif stime.endswith("m"):
			sec = sec + (int(stime[:-1])*60)
		elif stime.endswith("s"):
			sec = sec + int(stime[:-1])
	return sec


