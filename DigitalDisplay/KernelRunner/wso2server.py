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

# #########################Configure Parent Logger##########################
import logging
from logging.handlers import RotatingFileHandler

# create logger
LOGGER = logging.getLogger('wso2server')
LOGGER.setLevel(logging.DEBUG)

# create file handler which logs even debug messages
f_handler = RotatingFileHandler('wso2server.log', mode='a', maxBytes=5 * 1024 * 1024, backupCount=2, encoding=None, delay=0)
f_handler.setLevel(logging.DEBUG)

# create console handler with a higher log level
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
import modules.colorstreamhandler

# #########################Import Other Modules##########################

from modules.xmltodict import *
from modules.resource_types import *
import time
import modules.version_control as version_control
import modules.kernel_utils as kernel_utils
from threading import Thread
import subprocess

# #########################Read the Configurations##########################


class ConfigError(Exception):
    pass


"""
This method will read the configuration from a specific xml file.
@throws IOError
"""


def read_config(file_name):
    global conf

    # XML -> JSON
    xml_to_dict = XmlToDict()

    try:
        with open(file_name) as fd:
            return xml_to_dict.parse(fd.read())
    except IOError as e:
        LOGGER.warning("***Input/Output Error occured while reading the configuration `" + file_name + "` file...!")
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

# #########################Running the Sequence##########################
"""
This method returns implementing class for specific resourceTypes.
@return resourceType
@throws NotImplementedError when resourceType is Unknown
"""
resource_types = vars()['ResourceTypeBase'].__subclasses__()  # get all subclasses implementing ResourceTypeBase


def get_resource_type(type_):
    try:
        resource_type = [cls for cls in resource_types if cls.__dict__['name'] == type_][0]
    except IndexError:
        raise NotImplementedError("Unknown ResourceType found: `" + type_ + "`")
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
    while (True):
        for resource in sequence:
            current_phase_resource = resource
            args = {'browser': ddwebbrowser,
                    'port': webbrowser_conf['Port'],
                    'bpath': webbrowser_conf['Path'],
                    'delay': 10}
            show_up_time = resource.time
            resource.run(args)
            time.sleep(kernel_utils.get_seconds(show_up_time))
            resource.stop(args)

# #########################Check for Content Updates##########################
"""
This method will be callback just before 
any program or content update take place.
"""


def safe_exit_handler():
    LOGGER.debug("Exiting...")
    args = {'delay': 0}
    if current_phase_resource:
        current_phase_resource.stop(args)
    subprocess.Popen("ps aux | grep 'httpserver.py' | awk '{print $2}' | xargs kill", shell=True)


"""
This method will return repository handler 
to execute update commands.
"""


def get_repo_handler(update_conf):
    # get all defined handlers
    try:
        handlers = conf['DigitalDisplay']['KernelRunner']['VCSHandlers']['Handler']
    except KeyError:
        LOGGER.debug("No VCSHandler found...!")
        raise

    # get handler name for the repository
    repo_handler_name = update_conf['Repository']['VCSHandler']

    # get handler for the repository
    repo_handler = [handler for handler in handlers if handler['@name'] == repo_handler_name]
    if len(repo_handler) == 0:
        raise ConfigError("Repo Handler " + repo_handler + " is not defined in VCSHandlers!")
    return repo_handler[0]


"""
This method will will initiate polling 
program or content updates.
"""


def poll_update():
    # Update KernelRunner
    try:
        kernel_conf = conf['DigitalDisplay']['KernelRunner']['UpdatePolicy']
        repo_handler = get_repo_handler(kernel_conf)
        version_control.update_kernel(kernel_conf, repo_handler, safe_exit_handler)
    except KeyError:
        # no update policy, ignore error
        LOGGER.debug("No update policy found for Kernel, skipping update...!")
        pass

    # Update Content
    try:
        content_conf = cont_conf['DigitalDisplay']['Content']['UpdatePolicy']
        repo_handler = get_repo_handler(content_conf)
        version_control.update_kernel(content_conf, repo_handler, safe_exit_handler)
    except KeyError:
        # no update policy, ignore error
        LOGGER.debug("No update policy found for Content, skipping update...!")
        pass


def main():
    LOGGER.info("Server  Starting...")

    # read configurations
    read_kernel_config()
    read_content_config()

    # start sequence
    main_sequence_queue = init_sequence()
    thread = Thread(target=run_sequence, args=[main_sequence_queue])
    thread.start()
    time.sleep(10)  # startup delay for pulling updates

    # start polling repositories
    poll_update()


if __name__ == '__main__':
    main()