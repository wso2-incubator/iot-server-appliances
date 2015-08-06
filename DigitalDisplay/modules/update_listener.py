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
import threading
import logging

import kernel_utils as kernel_utils


LOGGER = logging.getLogger('wso2server.version_control')


class VersionControlError(Exception):
    pass


class Repository(object):
    def replace_params(self, str_):
        str_.replace("$dir", self.repo_name)
        return str_.replace("$url", self.repo_url)

    def __init__(self, repo_name_, repo_url_, repo_path_, repo_conf_):

        self.repo_name = repo_name_
        self.repo_url = repo_url_
        self.repo_commands = repo_conf_['Commands']
        self.local_repo_base_path = repo_path_
        # local_repo_base_path = os.getcwd()
        self.local_repo_path = os.path.join(self.local_repo_base_path, self.repo_name)
        self.init()

    def __makedirs(self, dir_path):
        """Adding os.makedirs() support for python 2.7.3"""
        if dir_path and not os.path.isdir(dir_path):
            head, tail = os.path.split(dir_path)
            self.__makedirs(head)
            os.mkdir(dir_path, 0777)

    def init_local_repo(self):
        LOGGER.debug("Creating local repository " + self.repo_name + "...!")
        self.__makedirs(self.local_repo_base_path)
        os.chdir(self.local_repo_base_path)
        command = self.replace_params(self.repo_commands["Init"])
        try:
            # call command and wait to complete
            subprocess.call(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
                                 close_fds=True, shell=True)
        except OSError, e:
            if e.errno == 2:
                LOGGER.debug("Command `" + command + "` Not found...!")
                sys.exit(1)
        except:
            LOGGER.debug("Error occurred on initializing repository: " + str(sys.exc_info()[1]))

    def update_local_repo(self):
        LOGGER.debug("Updating local repo " + self.repo_name + "...")
        os.chdir(self.local_repo_path)
        command = self.replace_params(self.repo_commands["Update"])
        try:
            p = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
                                 close_fds=True, shell=True)
            p.wait()
        except OSError, e:
            if e.errno == 2:
                LOGGER.warning("Command `" + command + "` Not found...!")
                sys.exit(1)
        except:
            LOGGER.warning("Error occurred updating local repository: " + str(sys.exc_info()[1]))

    def check_remote_changes(self):
        os.chdir(self.local_repo_path)

        command = self.replace_params(self.repo_commands["LocalRevision"])
        try:
            local_rev = subprocess.check_output(command, stderr=subprocess.PIPE, shell=True)
            local_rev = local_rev.strip()
        except subprocess.CalledProcessError:
            LOGGER.warning(
                "Error occurred on local: " + str(sys.exc_info()[1]))
            return False

        command = self.replace_params(self.repo_commands["RemoteRevision"])
        try:
            remote_rev = subprocess.check_output(command, stderr=subprocess.PIPE, shell=True)
            remote_rev = remote_rev.strip()
        except OSError, e:
            if e.errno == 2:
                LOGGER.warning("Command `" + command + "` Not found...!")
                sys.exit(1)  # no need to continue
        except subprocess.CalledProcessError:
            LOGGER.warning(
                "Error occurred on remote: " + str(sys.exc_info()[1]))
            return False

        LOGGER.debug(self.repo_name)
        LOGGER.debug("local rev: '" + local_rev + "'")
        LOGGER.debug("remote_rev: '" + remote_rev + "'")

        if local_rev == remote_rev:
            return False  # no changes needed
        elif local_rev == "" or remote_rev == "":
            return False  # no changes needed
        else:
            return True  # changes needed
            LOGGER.debug("changes detected")

    def init(self):
        if not (os.path.exists(self.local_repo_path)):
            self.init_local_repo()


class UpdateListener(object):
    lock = None

    @staticmethod
    def __get_repo_handler(handlers, update_conf):
        """
        Returns repository handler to execute update commands.
        """
        try:
            # get handler name for the repository
            repo_handler_name = update_conf['Repository']['VCSHandler']
        except KeyError:
            LOGGER.debug("No VCSHandler found...!")
            raise

        # get handler for the repository
        repo_handler = [handler for handler in handlers if handler['@name'] == repo_handler_name]
        if len(repo_handler) == 0:
            raise VersionControlError(
                "Repo Handler " + repo_handler_name + " is not defined in VCSHandlers!")
        return repo_handler[0]

    @staticmethod
    def execute_copy_script(dd_kernel_repo_name):
        if not (os.path.exists(kernel_utils.temp_path)):
            os.mkdir(kernel_utils.temp_path)

        os.chdir(kernel_utils.scripts_path)
        os.system("sh update_script.sh '" + kernel_utils.base_path+"' "+dd_kernel_repo_name)
        LOGGER.debug("running update_script.sh")

    @staticmethod
    def update(handlers, update_conf, lock, safe_exit_handler=None):
        """
        Initiates polling program or content updates.
        """
        repo_handler = UpdateListener.__get_repo_handler(handlers, update_conf)
        lock.acquire()
        dd_kernel_folder = kernel_utils.temp_path

        try:
            dd_kernel_repo_name = update_conf['Repository']['Name']
            dd_kernel_repo_url = update_conf['Repository']['Url']
            dd_kernel_poll_int = update_conf['PollingInterval']
        except KeyError:
            lock.release()
            raise VersionControlError("Error in reading UpdatePolicy for Kernel...!")

        git = Repository(dd_kernel_repo_name, dd_kernel_repo_url, dd_kernel_folder, repo_handler)

        if git.check_remote_changes():
            git.update_local_repo()
            if safe_exit_handler:
                safe_exit_handler()
            lock.release()
            UpdateListener.execute_copy_script(dd_kernel_repo_name)

        lock.release()
        # repeat it
        threading.Timer(kernel_utils.get_seconds(dd_kernel_poll_int), UpdateListener.update,
                        [handlers, update_conf, lock, safe_exit_handler]).start()
