#!/usr/bin/env python

from Publisher import *

ip = '10.100.7.38'	# IP address of the server
port = 7713		# Thrift listen port of the server
username = 'admin'	# username
password = 'admin' 	# passowrd 

publisher = Publisher()

# Initialize publisher with ip and port of server
publisher.init(ip, port)

# Connect to server with username and password
publisher.connect(username, password)

# Define stream definition
streamDefinition = "{ 'name':'org.wso2.iot.statistics.device.pin.data', 'version':'1.0.0', 'nickName': 'IoT Connected Device Pin Data', 'description': 'Pin Data Received', 'tags': ['arduino', 'led13'], 'metaData':[ {'name':'ipAdd','type':'STRING'},{'name':'deviceType','type':'STRING'},{'name':'owner','type':'STRING'}, {'name':'time','type':'STRING'}], 'payloadData':[ {'name':'macAddress','type':'STRING'}, {'name':'pin','type':'STRING'}, {'name':'pinValue','type':'STRING'}, {'name':'description','type':'STRING'}] }";
publisher.defineStream(streamDefinition)

# Publish sample message
publisher.publish("Test message form python client")

# Disconnect
publisher.disconnect()


