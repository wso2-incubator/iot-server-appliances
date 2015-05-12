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

import logging
from logging.handlers import RotatingFileHandler

# create logger
LOGGER = logging.getLogger('wso2server')
LOGGER.setLevel(logging.DEBUG)
# create file handler which logs even debug messages
fh = RotatingFileHandler('wso2server.log', mode='a', maxBytes=5*1024*1024, 
                                 backupCount=2, encoding=None, delay=0)
fh.setLevel(logging.DEBUG)
# create console handler with a higher log level
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
# create formatter and add it to the handlers
formatter = logging.Formatter('%(asctime)s - %(levelname)s: %(name)s.%(funcName)s()@%(lineno)d: %(message)s')
fh.setFormatter(formatter)
ch.setFormatter(formatter)
# add the handlers to the logger
LOGGER.addHandler(fh)
LOGGER.addHandler(ch)

from modules.xmltodict import *
import sys
from modules.resource_types import *
import time
import modules.ddbrowser as ddbrowser
import modules.version_control as version_control
import modules.kernel_utils as kernel_utils
import os
from threading import Thread
import subprocess


##########################Read the Configurations##########################

class ConfigError(Exception):
    pass

"""
This method will read the configuration from a specific xml file.
@throws IOError
"""
def read_config(file_name):
	global conf

	#XML -> JSON
	xmlToDict = XmlToDict()

	try:
	    with open(file_name) as fd:
	 		return xmlToDict.parse(fd.read())
	except IOError as e:
	    LOGGER.warning("***Input/Output Error occured while reading the configuration file...!")
	    raise
	except:
	    LOGGER.warning("***Unexpected error while reading config file:", sys.exc_info()[0])
	    raise

conf = {}
def read_kernel_config():
	global conf
	conf = read_config(kernel_utils.kernel_path + '/conf/digital_display_kernelrunner.xml')

cont_conf = {}
def read_content_config():
	global cont_conf
	cont_conf = read_config(kernel_utils.content_path + '/conf/digital_display_content.xml')

##########################Running the Sequence##########################
"""
This method returns implementing class for specific resourceTypes.
@return resourceType
@throws NotImplementedError when resourceType is Unknown
"""
resource_types = vars()['ResourceTypeBase'].__subclasses__()#get all subclasses implementing ResourceTypeBase
def get_resource_type(type_):
	try:
		resource_type = [cls for cls in resource_types if cls.__dict__['name']==type_][0]
	except IndexError:
		raise NotImplementedError("Unknown ResourceType found: `"+type_+"`")
	return resource_type


"""
This method will return a new sequence queue.
@return sequence_queue
@throws ConfigError
"""
def init_sequence():
	try:
		resources = cont_conf['DigitalDisplay']['Content']['DisplaySequence']['Resource']
	except KeyError:
		raise ConfigError("No 'Resource' elements found under the 'DisplaySequence'...!")
	sequence_queue = []
	if type(resources) is list:
		for resource in resources:
			args = [(key, resource[key]) for key in resource.keys() if key.startswith("@")]
			rv = get_resource_type(resource['@type'])
			instance = rv(args)
			sequence_queue.append(instance)
	else:
		args = [(key, resources[key]) for key in resources.keys() if key.startswith("@")]
		rv = get_resource_type(resources['@type'])
		instance = rv(args)
		sequence_queue.append(instance)
	return sequence_queue

current_phase_resource = None
def run_sequence(sequence):
	global current_phase_resource
	webbrowser_conf = conf['DigitalDisplay']['KernelRunner']['WebBrowser']
	ddwebbrowser = ddbrowser.start(webbrowser_conf)
	time.sleep(10)
	while(True):
		for resource in sequence:
			current_phase_resource = resource
			args = {'browser':ddwebbrowser, 
					'port': webbrowser_conf['Port'],
					'bpath': webbrowser_conf['Path'],
					'delay' : 10}
			show_up_time = resource.time
			resource.run(args)
			time.sleep(kernel_utils.get_seconds(show_up_time))
			resource.stop(args)

LOGGER.info("Server  Starting...::")
read_kernel_config()
read_content_config()
main_sequence_queue = init_sequence()
thread = Thread(target = run_sequence, args = [main_sequence_queue])
thread.start()

##########################Check for Content Updates##########################
time.sleep(10); #startup delay for pulling updates
def safe_exit_handler():
	LOGGER.debug("Exiting...")
	args = {'delay' : 0}
	if(current_phase_resource):
		current_phase_resource.stop(args)
	subprocess.Popen("ps aux | grep 'httpserver.py' | awk '{print $2}' | xargs kill", shell=True)

try:
	kernel_conf = conf['DigitalDisplay']['KernelRunner']['UpdatePolicy']
	repo_handler = kernel_conf['Repository']['VCSHandler']
	handlers = conf['DigitalDisplay']['KernelRunner']['VCSHandlers']['Handler']
	repo_conf = [handler for handler in handlers if handler['@name']==repo_handler]
	if len(repo_conf)==0: raise ConfigError("Repo Handler "+repo_handler+" is not defined in VCSHandlers!")
	version_control.update_kernel(kernel_conf, repo_conf[0], safe_exit_handler)
except KeyError:
	#no update policy
	#ignore error
	LOGGER.debug("No update policy found for Kernel, skipping update...!")
	pass

try:
	content_conf = cont_conf['DigitalDisplay']['Content']['UpdatePolicy']
	repo_handler = content_conf['Repository']['VCSHandler']
	repo_conf = [handler for handler in handlers if handler['@name']==repo_handler]
	if len(repo_conf)==0: raise ConfigError("Repo Handler "+repo_handler+" is not defined in VCSHandlers!")
	version_control.update_content(content_conf, repo_conf[0], safe_exit_handler)
except KeyError:
	#no update policy
	#ignore error
	LOGGER.debug("No update policy found for Content, skipping update...!")
	pass
	
	
