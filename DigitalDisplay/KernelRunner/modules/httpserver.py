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
import socket
import errno
import sys
import logging
import subprocess

LOGGER = logging.getLogger('wso2server.httpserver')


class DDHTTPRequestHandler(SimpleHTTPServer.SimpleHTTPRequestHandler):
    def end_headers(self):
        self.send_my_headers()
        SimpleHTTPServer.SimpleHTTPRequestHandler.end_headers(self)

    def send_my_headers(self):
        self.send_header("Cache-Control", "no-cache, no-store, must-revalidate")
        self.send_header("Pragma", "no-cache")
        self.send_header("Expires", "0")


"""
This module is intended to run through python commandline.
python httpserver.py {path} {port}
"""
path = ""
port = 8000
arg_length = len(sys.argv)

if arg_length >= 2:
    path = sys.argv[1]
if arg_length >= 3:
    port = int(sys.argv[2])

LOGGER.debug("path:" + sys.argv[1])
LOGGER.debug("port:" + sys.argv[2])

if path:
    LOGGER.debug("Changing path to: " + path)
    try:
        os.chdir(path)
    except OSError, e:
        if e.errno == 2:
            LOGGER.warning("`" + path + "` does not exist...!")


def start_server():
    SocketServer.TCPServer.allow_reuse_address = True
    handler = DDHTTPRequestHandler
    httpd = SocketServer.TCPServer(("", port), handler)
    LOGGER.info("Serving @port" + str(port) + "...")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        LOGGER.info("Stopping server...")
        httpd.shutdown()

try:
    start_server()
except socket.error as e:
    print e.errno
    if e.errno == errno.EADDRINUSE:
        subprocess.Popen("ps aux | grep 'httpserver.py' | awk '{print $2}' | xargs kill -9", shell=True)
        start_server()