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
import signal
import logging
import kernel_utils as kernel_utils


LOGGER = logging.getLogger('wso2server.player')


class BasePlayer(object):
    """Parent class for all players. Do not use directly."""

    name = "undefined"
    args = ['%s']

    def __init__(self, path_="", video_file="", exists=False):
        if video_file=="":
            raise ValueError("Video File shoud not be null!")
        self.path = path_
        self.process = subprocess.Popen([path_ , video_file], preexec_fn=os.setsid)

    def kill(self):
        LOGGER.info("-------player kill called-------")
        self.process.kill()
        self.process.terminate()
        os.killpg(self.process.pid, signal.SIGTERM)

class OMXPlayer(BasePlayer):
    name = "omxplayer"
    args = ["%s"]

"""
This method returns implementing class for specific playerTypes.
@return playerType
@throws NotImplementedError when playerType is Unknown
"""
player_types = vars()['BasePlayer'].__subclasses__()  # get all subclasses implementing BasePlayer


def get_player_type(type_):
    try:
        player_type = [cls for cls in player_types if cls.__dict__['name'] == type_][0]
    except IndexError:
        return BasePlayer
    return player_type


current_player = None

def kill():
    current_player.kill()

def start(player_conf, video_file):
    global current_player
    player_class = get_player_type(player_conf['Name'])
    exists_ = kernel_utils.check_process(player_conf['Path'])
    current_player = player_class(player_conf['Path'], video_file, exists=exists_)
    return current_player

