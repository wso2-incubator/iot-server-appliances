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

from modules.xmltodict import *
import sys
from modules.resource_types import *
import time
import webbrowser
import modules.version_control as version_control
import modules.kernel_utils as kernel_utils
import os
from threading import Thread
import logging
import modules.colorstreamhandler

logging.basicConfig(level=logging.DEBUG)
LOGGER = logging.getLogger('org.wso2.iot.dd.raspi.server')

##########################Read the Configurations##########################

class ConfigError(Exception):
    pass

"""
This method will read the configuration from a specific xml file.
@throws IOError
"""
conf = {}
def read_config():
	global conf

	#XML -> JSON
	xmlToDict = XmlToDict()

	try:
	    with open('conf/digital_display.xml') as fd:
	 		conf = xmlToDict.parse(fd.read())
	except IOError as e:
	    LOGGER.warning("***Input/Output Error occured while reading the configuration file...!")
	    raise
	except:
	    LOGGER.warning("***Unexpected error while reading config file:", sys.exc_info()[0])
	    raisedd

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
		resources = conf['DigitalDisplay']['DisplaySequence']['Resource']
	except KeyError:
		raise ConfigError("No 'Resource' elements found under the 'DisplaySequence'...!")
	sequence_queue = []
	for resource in resources:
		args = [(key, resource[key]) for key in resource.keys() if key.startswith("@")]
		rv = get_resource_type(resource['@type'])
		instance = rv(args)
		sequence_queue.append(instance)
	return sequence_queue

current_phase_resource = None
def run_sequence(sequence):
	global current_phase_resource
	webbrowser_conf = conf['DigitalDisplay']['WebBrowser']
	while(True):
		for resource in sequence:
			current_phase_resource = resource
			args = {'browser':webbrowser_conf['Name'], 
					'port': webbrowser_conf['Port'],
					'bpath': webbrowser_conf['Path']}
			show_up_time = resource.time
			resource.run(args)
			time.sleep(kernel_utils.get_seconds(show_up_time))
			resource.stop(args)

LOGGER.info("Server Starting...")
read_config()
main_sequence_queue = init_sequence()
thread = Thread(target = run_sequence, args = [main_sequence_queue])
thread.start()

##########################Check for Content Updates##########################

def safe_exit_handler():
	LOGGER.debug("Exiting...")
	current_phase_resource.stop(None)

try:
	kernel_conf = conf['DigitalDisplay']['UpdatePolicy']['Kernel']
	repo_handler = kernel_conf['Repository']['VCSHandler']
	handlers = conf['DigitalDisplay']['VCSHandlers']['Handler']
	repo_conf = [handler for handler in handlers if handler['@name']==repo_handler]
	if len(repo_conf)==0: raise ConfigError("Repo Handler "+repo_handler+" is not defined in VCSHandlers!")
	version_control.update_kernel(kernel_conf, repo_conf[0], safe_exit_handler)
except KeyError:
	#no update policy
	#ignore error
	LOGGER.debug("No update policy found for Kernel, skipping update...!")
	pass

try:
	content_conf = conf['DigitalDisplay']['UpdatePolicy']['Content']
	repo_handler = content_conf['Repository']['VCSHandler']
	repo_conf = [handler for handler in handlers if handler['@name']==repo_handler]
	if len(repo_conf)==0: raise ConfigError("Repo Handler "+repo_handler+" is not defined in VCSHandlers!")
	version_control.update_content(content_conf, repo_conf[0], safe_exit_handler)
except KeyError:
	#no update policy
	#ignore error
	LOGGER.debug("No update policy found for Content, skipping update...!")
	pass
	
	