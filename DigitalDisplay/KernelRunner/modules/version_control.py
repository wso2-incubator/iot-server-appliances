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
import kernel_utils as kernel_utils
import logging
import colorstreamhandler

logging.basicConfig(level=logging.DEBUG)
LOGGER = logging.getLogger('org.wso2.iot.dd.raspi.version_control')

class VersionError(Exception):
    pass

class Repository(object):

	def replace_params(self, str_):
		return str_.replace("$url", self.repo_url)

	def __init__(self, repo_name_, repo_url_, repo_path_, repo_conf_):
		
		self.repo_name = repo_name_
		self.repo_url = repo_url_
		self.repo_commands = repo_conf_['Commands']
		self.local_repo_base_path = repo_path_
		#local_repo_base_path = os.getcwd()
		self.local_repo_path = os.path.join(self.local_repo_base_path, self.repo_name)
		self.init()

	def init_local_repo(self):
		LOGGER.debug("Creating local repo "+ self.repo_name +"...")
		os.chdir(self.local_repo_base_path)
		command = self.replace_params(self.repo_commands["Init"])
		try:
			p = subprocess.check_call(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, close_fds=True, shell=True)
		except OSError, e:
			if e.errno==2:
				LOGGER.warning("Command Not found...!")
				sys.exit(1)
		except:
			LOGGER.warning("Error occured on init_local_repo()")	

	def update_local_repo(self):
		LOGGER.debug("Updating local repo "+ self.repo_name +"...")
		os.chdir(self.local_repo_path)
		command = self.replace_params(self.repo_commands["Update"])
		try:
			p = subprocess.check_call(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, close_fds=True, shell=True)
		except OSError, e:
			if e.errno==2:
				LOGGER.warning("Command Not found...!")
				sys.exit(1)
		except:
			LOGGER.warning("Error occured on update_local_repo()")

	def check_remote_changes(self):
		os.chdir(self.local_repo_path)
		
		command = self.replace_params(self.repo_commands["LocalRevision"])
		try:
			p = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, shell=True)
			local_rev = p.stdout.readline().strip()
			p.wait()
		except:
			LOGGER.warning("Error occured on check_remote_changes()")	

		command = self.replace_params(self.repo_commands["RemoteRevision"])
		try:
			p = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, shell=True)
			remote_rev = p.stdout.readline().strip()
			p.wait()
		except OSError, e:
			if e.errno==2:
				LOGGER.warning("Command Not found...!")
				sys.exit(1)
		except:
			LOGGER.warning("Error occured on init_local_repo()")	

		LOGGER.debug("local rev: '" + local_rev + "'")
		LOGGER.debug("remote_rev: '" + remote_rev + "'")

		if(local_rev==remote_rev): 
			return False #no changes needed
		elif(local_rev=="" or remote_rev==""):
			return False #no changes needed
		else:
			return True #changes needed
			LOGGER.debug("changes detected")

	def init(self):
		if not(os.path.exists(self.local_repo_path)):
			self.init_local_repo()

#git = GitHubRepository("dd-webcontent", "https://github.com/rasika90/dd-webcontent.git", os.getcwd())
#git.update_local_repo()

if not (os.path.exists(kernel_utils.temp_path)):
	os.mkdir(kernel_utils.temp_path)

def restart_kernel():
	os.chdir(os.path.normpath(os.path.dirname(os.path.abspath(__file__))))
	LOGGER.debug("current pid:"+str(os.getpid()))
	subprocess.Popen("sh copy_kernel.sh " + kernel_utils.base_path, shell=True)

def update_kernel(kernel_conf, repo_conf, function):
	global dd_kernel_last_revision
	dd_kernel_folder = kernel_utils.temp_path
	
	try:
		dd_kernel_repo_name = kernel_conf['Repository']['Name']
		dd_kernel_repo_url = kernel_conf['Repository']['Url']
		dd_kernel_poll_int = kernel_conf['PollingInterval']
	except KeyError:
		raise VersionError("Error in reading UpdatePolicy for Kernel...!")

	git = Repository(dd_kernel_repo_name, dd_kernel_repo_url, dd_kernel_folder, repo_conf)
	if(git.check_remote_changes()): 
		git.update_local_repo()
		function()
		restart_kernel()
	threading.Timer(kernel_utils.get_seconds(dd_kernel_poll_int), update_kernel, [kernel_conf,repo_conf,function]).start()


def update_content(content_conf, repo_conf, function):
	dd_content_folder = kernel_utils.temp_path
	
	try:
		dd_content_repo_name = content_conf['Repository']['Name']
		dd_content_repo_url = content_conf['Repository']['Url']
		dd_content_poll_int = content_conf['PollingInterval']
	except KeyError:
		raise VersionError("Error in reading UpdatePolicy for Content...!")
	
	git = Repository(dd_content_repo_name, dd_content_repo_url, dd_content_folder, repo_conf)
	if(git.check_remote_changes()): 
		git.update_local_repo()
		os.chdir(os.path.normpath(os.path.dirname(os.path.abspath(__file__))))
		subprocess.Popen("sh copy_content.sh "+kernel_utils.base_path, shell=True)
	threading.Timer(kernel_utils.get_seconds(dd_content_poll_int), update_content, [content_conf,repo_conf,function]).start()
