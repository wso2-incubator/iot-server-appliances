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
import re
import time
import webbrowser
import logging

class ConfigError(Exception):
    pass

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
	    logging.warning("***Input/Output Error occured while reading the configuration file...!")
	    raise
	except:
	    logging.warning("***Unexpected error while reading config file:", sys.exc_info()[0])
	    raise

	#print conf['DigitalDisplay']['Name']
	#print conf['DigitalDisplay']['ServerKey']
	#print conf['DigitalDisplay']['Version']
	#print conf['DigitalDisplay']['ResourcesTypes']
	#print conf['DigitalDisplay']['DisplaySequence']

#############Check for Content Updates#############

"""
This method will return a new sequence queue.
@return sequence_queue
@throws ConfigError
"""
def make_sequence():
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

def run_sequence(sequence):
	while(True):
		for resource in sequence:
			#b = webbrowser.get('epiphany')
			args = {'browser':conf['DigitalDisplay']['WebBrowser']['Name'], 
					'port': conf['DigitalDisplay']['WebBrowser']['Port'],
					'bpath': conf['DigitalDisplay']['WebBrowser']['Path']}
			show_up_time = resource.time
			resource.run(args)
			time.sleep(get_seconds(show_up_time))
			resource.stop(args)

read_config()
main_sequence_queue = make_sequence()
run_sequence(main_sequence_queue)
#print get_seconds("1h 1s")

#print 'Instance:', isinstance(RegisteredImplementation(), PluginBase)
#print([cls.__name__ for cls in vars()['ResourceTypeBase'].__subclasses__() if cls.__name__ == "ResourceTypeIFrame"])
#print ResourceTypeBase.__dict__
#print([cls.__name__ for cls in vars()['ResourceTypeBase'].__subclasses__())
#print([cls for cls in vars()['ResourceTypeBase'].__subclasses__()])
#print 'Instance:', isinstance(ResourceTypeIFrame(), ResourceTypeBase)


