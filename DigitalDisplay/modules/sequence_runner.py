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
import mechanize
import cookielib
import download_resources
import json
import logging
import threading
import datetime


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
    attempt = 1
    note_show_up_time = 0

    def __init__(self, resources_conf, web_browser):

        self.mechanize_browser = mechanize.Browser()

        self.cj = cookielib.LWPCookieJar()
        self.mechanize_browser.set_cookiejar(self.cj)

        if resources_conf is None:
            raise SequenceRunnerError("Initialization error: resources_conf cannot be null!")
        if web_browser is None:
            raise SequenceRunnerError("Initialization error: web_browser cannot be null!")
        self.current_resources_conf = resources_conf
        self.current_web_browser = web_browser
        # get all subclasses implementing ResourceTypeBase
        self.resource_types = resource_types.get_all_resource_types()

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

        sequence = self.__create_sequence(self.current_resources_conf)
        sequence_id = 0

        while True:

            # background thread
            if time.time() - self.lastsave > 3600 and self.can_download():
                LOGGER.info("Start Background Thread")
                day = datetime.datetime.now()
                download_thread = threading.Thread(target=self.download_images, args=(day,))
                download_thread.start()
                self.lastsave = time.time()

            # check notification page
            if sequence_id >= (len(sequence) - 1):
                if self.is_notifications() and self.can_show_notifications() and self.attempt == 1:
                    self.attempt += 1
                else:
                    sequence_id = 0
                    self.remove_notifications()

            # get the resource
            resource = sequence[sequence_id]
            SequenceRunner.current_phase_resource = resource

            # setting arguments
            args = {'browser': self.current_web_browser,
                    'port': self.current_web_browser.port,
                    'browser_path': self.current_web_browser.path,
                    'delay': 10}

            # invoke run on the resource
            resource.run(args)

            if self.attempt>1:
                # change the sleeping time when notification is available
                notification_show=unicode(str(self.note_show_up_time*52)+'s')
                time.sleep(kernel_utils.get_seconds(notification_show))
            else:
                # sleep for showup time
                show_up_time = resource.time
                time.sleep(kernel_utils.get_seconds(show_up_time))

            # invoke stop on the resource
            resource.stop(args)

            sequence_id += 1

    def download_images(self, day):
        mechanize_browser = download_resources.SalesRemoteData(self.mechanize_browser)
        mechanize_browser.download_data(day)

    def remove_notifications(self):
        if self.attempt > 1:
            with open('../resources/www/page14/mailing.json', 'w') as inFile:
                inFile.truncate()
                self.attempt = 1

    def is_notifications(self):
        with open('../resources/www/page14/mailing.json', 'r') as inFile:
            try:
                colors = json.load(inFile)
                self.note_show_up_time = len(colors)
                return True
            except ValueError:
                return False

    def can_download(self):
        day = datetime.datetime.now()
        if day.isoweekday() in range(1, 6) and day.hour in range(6, 18):
            return True
        else:
            return False

    def can_show_notifications(self):
        day = datetime.datetime.now()
        if day.isoweekday() in range(1, 6) and day.hour in range(10, 16):
            return True
        else:
            return False

    # def update_content(self):
    #
    #     while True:
    #         day = datetime.datetime.now()
    #         # background thread
    #         if time.time() - self.lastsave > 3600 and day.isoweekday() in range(1, 6) and day.hour in range(6, 18):
    #             LOGGER.info("Start Background Thread")
    #             download_thread = threading.Thread(target=self.download_images, args=(day,))
    #             download_thread.start()
    #             self.lastsave = time.time()
