#!/usr/bin/env python

import sys
sys.path.append('BAMPythonPublisher')
sys.path.append('BAMPythonPublisher/gen-py')

from Publisher import *
from BAMPublisher import *

import serial
import time
import datetime

BAM_IP = 'localhost'					# IP address of the BAM/CEP server
BAM_PORT = 7713							# Thrift listen port of the server
BAM_UNAME = 'admin'						# Username to connect to server
BAM_PASSWRD = 'admin' 					# Passowrd to connect to server
BT_PORT = "/dev/tty.HC-06-DevB"			# Port to which the Bluetooth Device is bound
BT_BAUDRATE = 9600						# Baud-Rate of the communication link between BT-Device and the machine


publisher = Publisher()

# Initialize publisher with ip and port of server
publisher.init(BAM_IP, BAM_PORT)

# Connect to server with username and password
publisher.connect(BAM_UNAME, BAM_PASSWRD)

# Define stream definition
streamDefinition = "{ 'name':'org.wso2.iot.statistics.indiana.sensor.data', 'version':'1.0.0', 'nickName': 'IoT Connected Device Pin Data', 'description': 'Pin Data Received', 'tags': ['arduino', 'led13'], 'metaData':[ {'name':'ipAdd','type':'STRING'},{'name':'deviceType','type':'STRING'},{'name':'owner','type':'STRING'}, {'name':'time','type':'STRING'}], 'payloadData':[ {'name':'macAddress','type':'STRING'}, {'name':'pin','type':'STRING'}, {'name':'pinValue','type':'STRING'}, {'name':'description','type':'STRING'}] }";

publisher.defineStream(streamDefinition)
myPublisher = BAMPublisher()

bluetoothSerial = serial.Serial( BT_PORT , BT_BAUDRATE )

bluetoothSerial.write("6"); # simple approximate time sync-- assumed latency is negligible considering sensor information
ts = time.time()
lines = bluetoothSerial.readline()
print lines

while True:
	lines = bluetoothSerial.readline()
	print lines+"\n"
	sensorData=lines.split(',')
	for line in sensorData:
		line = line.split(':')
		sensor = line[0]
		value = line[1]
		time = ts+float(line[2])
		print sensor
		print value
		print time

		myPublisher.setPublishStream(time, sensor, value)
	
		# print myPublisher.metaData
		# print myPublisher.payloadData
		
		# Publish message
		publisher.publish(myPublisher.metaData, myPublisher.payloadData)

# myPublisher.setPublishStream("TIME", "SONAR", "30")
# publisher.publish(myPublisher.metaData, myPublisher.payloadData)

# Disconnect
publisher.disconnect()


