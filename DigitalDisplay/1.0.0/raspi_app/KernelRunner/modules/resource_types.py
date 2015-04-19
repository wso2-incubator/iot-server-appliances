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

"""
Abstract class for Resource Types. Every specific resource type
should override and implement this class.

NOTE:
NotImplementedError will be raised at run-time not in compile-time.
"""

import os
import subprocess
import time
from threading import Timer
import sys
from ddbrowser import *
import logging

"""
This method returns implementing class for specific browserTypes.
@return browserType
@throws NotImplementedError when browserType is Unknown
"""
browser_types = vars()['BaseBrowser'].__subclasses__()#get all subclasses implementing BaseBrowser
def _get_browser_type(type_):
	try:
		browser_type = [cls for cls in browser_types if cls.__dict__['name']==type_][0]
	except IndexError:
		return BaseBrowser
	return browser_type

def _get_sid():
	setsid = getattr(os, 'setsid', None)
	if not setsid:
		setsid = getattr(os, 'setpgrp', None)
	return setsid

def _open_browser(url_, webbrowser_, path_=""):
	rv = _get_browser_type(webbrowser_)
	browser_instance = rv(path_)
	return browser_instance.open(url_)

def _start_server(_path, port = "8000"):
	httpd = subprocess.Popen(["python",  os.path.join(os.getcwd(),"modules", "httpserver.py"), _path, port], close_fds=True, preexec_fn=_get_sid())
	return httpd

def _kill_process(process, time_=None):
	if not (time_):
		logging.debug("killing :" + str(process.pid))
		process.kill()
	else:
		logging.debug("killing sheduled")
		t = Timer(time_, _kill_process, [process])
		t.start()

class ResourceTypeBase(object):

	"""
	This name attribute should be unqiue and should be mapped with 
	the `type` attribute in Resource element under the Display Sequence.
	"""
	def name(self):
		raise NotImplementedError("`name` attribute is not implemented...!") 

	"""
	This method is called when initiating the sequence.
	@param args: is a list of tuples with key value pairs
	"""
	def __init__(self, args):
		raise NotImplementedError("`__init__()` method is not implemented...!") 
    
	"""
	This method is called when the sequence is running the specific 
 	resource type.
 	@param args: is a list of tuples with key value pairs
 	"""
	def run(self, args):
		raise NotImplementedError("`run()` method is not implemented...!") 

	"""
	This method is called when the sequence is stop and switch to 
	another specific resource type.
	@param args: is a list of tuples with key value pairs
	"""
	def stop(self, args):
		raise NotImplementedError("`stop()` method is not implemented...!") 

"""
Implementation class for `Folder` resource type.
"""    
class ResourceTypeFolder(ResourceTypeBase):
	name = 'folder'
	time = ''
	def __init__(self, args):
		for arg in args:
			if arg[0] == "@time":
				self.time = arg[1]
			elif arg[0] == "@path":
				self.path = arg[1]
	def run(self, args):
		logging.debug("folder: run()")
		webbrowser_ = args['browser']
		port = args['port']
		bpath_ = args['bpath']
		self.server = _start_server(self.path, port)
		self.browser = _open_browser("http://localhost:"+port, webbrowser_, bpath_)
	def stop(self, args):
		logging.debug("folder: stop()")
		self.server.kill()
		_kill_process(self.browser, 5)

"""
Implementation class for `Page` resource type.
"""   
class ResourceTypePage(ResourceTypeBase):
	name = 'page'
	def __init__(self, args):
		for arg in args:
			if arg[0] == "@time":
				self.time = arg[1]
			elif arg[0] == "@path":
				self.path = arg[1]
	def run(self, args):
		logging.debug("page: run()")
		webbrowser_ = args['browser']
		bpath_ = args['bpath']
		self.browser =_open_browser(self.path, webbrowser_, bpath_)
	def stop(self, args):
		logging.debug("page: stop()")
		_kill_process(self.browser, 5)

"""
Implementation class for `URL` resource type.
"""   
class ResourceTypeUrl(ResourceTypeBase):
	name = 'url'
	def __init__(self, args):
		for arg in args:
			if arg[0] == "@time":
				self.time = arg[1]
			elif arg[0] == "@url":
				self.url = arg[1]

	def run(self, args):
		logging.debug("url: run()")
		webbrowser_ = args['browser']
		bpath_ = args['bpath']
		self.browser =_open_browser(self.url, webbrowser_, bpath_)

	def stop(self, args):
		logging.debug("url: stop()")
		_kill_process(self.browser, 5)

"""
Implementation class for `IFrame` resource type.
"""   
class ResourceTypeIFrame(ResourceTypeBase):
	name = 'iframe'
	def __init__(self, args):
		for arg in args:
			if arg[0] == "@time":
				self.time = arg[1]
			elif arg[0] == "@url":
				self.url = arg[1]
	def run(self, args):
		logging.debug("iframe: run()")
	def stop(self, args):
		logging.debug("iframe: stop()")
		
#print 'Subclass:', issubclass(ResourceTypeIFrame, ResourceTypeBase)
#print 'Instance:', isinstance(ResourceTypeIFrame(), ResourceTypeBase)
