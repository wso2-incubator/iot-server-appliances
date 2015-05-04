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
import colorstreamhandler

logging.basicConfig(level=logging.DEBUG)
LOGGER = logging.getLogger('org.wso2.iot.dd.raspi.ddbrowser')


class BaseBrowser(object):
    """Parent class for all browsers. Do not use directly."""

    name = "undefined"
    args = ['%s']

    def __init__(self, path_=""):
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
	    	LOGGER.debug(pargs)
	    	p = subprocess.Popen(pargs, close_fds=True, preexec_fn=self._get_sid())
	    except OSError, e:
	    	if e.errno == 2:
	    		LOGGER.warning("Webbrowser `"+ self.name +"` at `"+ self.path +"` not found...!")
	    	else:
	    		raise
	    	sys.exit(0)
	    return p

class MidoriBrowser(BaseBrowser):
	
	name = "midori"
	#args = ["-e","Fullscreen", "-a", "%s"]
	args = ["-a", "%s"]
	
class ChromeBrowser(BaseBrowser):
	
	name = "chrome"
	args = ["--app", "%s"]
	