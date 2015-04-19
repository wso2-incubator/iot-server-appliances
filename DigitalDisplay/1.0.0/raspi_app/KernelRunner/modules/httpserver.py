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
import os
import SimpleHTTPServer
import SocketServer
import logging
import sys

"""
This module is intended to run through python commandline.
python httpserver.py {path} {port}
"""
path = ""
port = 8000
arg_length = len(sys.argv)
if(arg_length>=2):
	path = sys.argv[1]
if(arg_length>=3):
	port = int(sys.argv[2])

if(path):
	logging.info("Changing path to "+path)
	try:
		os.chdir(path)
	except OSError, e:
		if e.errno == 2:
			logging.warning("File/Folder does not exist...!")

SocketServer.TCPServer.allow_reuse_address = True
Handler = SimpleHTTPServer.SimpleHTTPRequestHandler
httpd = SocketServer.TCPServer(("", port), Handler)
logging.info("Serving at port" + str(port))
httpd.serve_forever()
