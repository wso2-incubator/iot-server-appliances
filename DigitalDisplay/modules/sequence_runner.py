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

To use, simply 'import sequence_runner' and run sequence away!
"""
import time
import logging
import modules.kernel_utils as kernel_utils
import modules.resource_types as resource_types

LOGGER = logging.getLogger('wso2server.resource_types')


class SequenceRunnerError(Exception):
    """SequenceRunner Exception class"""
    pass


class SequenceRunner(object):
    """
    SequenceRunner class for executing sequence of resources.
    """
    current_phase_resource = None
    lastsave = -3700
    attempt = 0
    note_show_up_time = 0
    note_showing = False
    first_hour_attempt = True
    second_hour_attempt = True
    sequence = None
    _instance = None
    current_resources_conf = None
    current_web_browser = None
    resource_types = None
    current_video_player = None

    # for implement singleton pattern
    def __new__(cls, *args, **kwargs):
        if not cls._instance:
            cls._instance = super(SequenceRunner, cls).__new__(cls, *args, **kwargs)
        return cls._instance

    def __get_resource_type(self, type_):
        """
        Returns implementing class for specific resourceTypes.
        @return resourceType
        @throws NotImplementedError when resourceType is Unknown
        """
        try:
            resource_type = [cls for cls in self.resource_types if cls.__dict__['name'] == type_][0]
        except IndexError:
            raise NotImplementedError("Unknown ResourceType found: `" + type_ + "`")
        return resource_type

    def set_all_conf(self, resource_conf, web_browser, video_player):
        self.set_current_resources_conf(resource_conf)
        self.set_web_browser(web_browser)
        self.set_current_video_player(video_player)
        self.set_resource_types()
        self.set_sequence()

    def set_current_video_player(self, video_player):
        self.current_video_player = video_player

    def set_current_resources_conf(self, resources_conf):
        if resources_conf is None:
            raise SequenceRunnerError("Initialization error: resources_conf cannot be null!")
        self.current_resources_conf = resources_conf

    def set_web_browser(self, web_browser):
        if web_browser is None:
            raise SequenceRunnerError("Initialization error: web_browser cannot be null!")
        self.current_web_browser = web_browser

    def set_resource_types(self):
        self.resource_types = resource_types.get_all_resource_types()

    def set_sequence(self):
        self.sequence = self.__create_sequence(self.current_resources_conf)

    def __create_sequence(self, resources_conf):
        """
        Returns a new sequence queue.
        @return sequence_queue
        @throws ConfigError
        """
        sequence_queue = []
        if type(resources_conf) is list:
            # multiple resources loop through it
            for resource in resources_conf:
                args = [(key, resource[key]) for key in resource.keys() if key.startswith("@")]
                rv = self.__get_resource_type(resource['@type'])
                instance = rv(args)
                sequence_queue.append(instance)
        else:
            # just one resource found
            args = [(key, resources_conf[key]) for key in resources_conf.keys() if
                    key.startswith("@")]
            rv = self.__get_resource_type(resources_conf['@type'])
            instance = rv(args)
            sequence_queue.append(instance)
        return sequence_queue

    def run_sequence(self):

        sequence_id = 0

        while True:

            try:

                # check notification page
                if sequence_id > (len(self.sequence) - 1):
                    sequence_id = 0

                # get the resource
                resource = self.sequence[sequence_id]
                SequenceRunner.current_phase_resource = resource

                # setting arguments
                args = {'browser': self.current_web_browser,
                        'port': self.current_web_browser.port,
                        'browser_path': self.current_web_browser.path,
                        'player_conf': self.current_video_player,
                        'delay': 10}

                # invoke run on the resource
                resource.run(args)

                show_up_time = resource.time
                time.sleep(kernel_utils.get_seconds(show_up_time))
                sequence_id += 1

                resource.stop(args)

            except Exception as e:
                LOGGER.warning(e)
                sequence_id += 1
                continue