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
Configuration Reader.

Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.

To use, simply 'import config_reader' and read configurations away!
"""
import logging
import sys

from modules.config.xmltodict import XmlToDict


LOGGER = logging.getLogger('wso2server.config_reader')


class ConfigError(Exception):
    """Configuration Exception class"""
    pass


class ConfigReader(object):
    """Configuration Reader class"""
    @staticmethod
    def read_config(file_name):
        """
        Read the configuration from a specific xml file.
        @throws IOError
        """

        # XML -> JSON
        xml_to_dict = XmlToDict()

        try:
            with open(file_name) as fd:
                return xml_to_dict.parse(fd.read())
        except IOError as e:
            LOGGER.warning(
                "***Input/Output Error occurred while reading the configuration `" + file_name
                + "` file...!", e)
            raise
        except:
            LOGGER.warning("***Error occurred while reading config file:", sys.exc_info()[0])
            raise ConfigError("Error occurred while reading config file:", sys.exc_info()[0])