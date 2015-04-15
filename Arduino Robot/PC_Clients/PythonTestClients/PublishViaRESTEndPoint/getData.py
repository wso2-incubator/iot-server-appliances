#! /usr/bin/python
import serial
import requests
import time
import datetime

serverIP = "localhost"
serverPort = "9763"
publisherEndpoint = "/ConnectedDevices-1.0.0/pushdata"

#"/pushdata/{ip}/{owner}/{type}/{mac}/{time}/{pin}/{value}")
deviceIP = "/192.168.1.999"
deviceOwner = "/SMEAN"
deviceType = "/ArduinoUNO"
deviceMAC = "/98:D3:31:80:38:D3"

publisherEndpoint = "http://" + serverIP + ":" + serverPort + publisherEndpoint + deviceIP + deviceOwner + deviceType + deviceMAC + "/"
# print publisherEndpoint

bluetoothSerial = serial.Serial( "/dev/tty.HC-06-DevB", baudrate=9600 )
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
		#time=datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S');
		print sensor
		print value
		print time

		currentResource = publisherEndpoint + str(long(round(time*1000)))+"/"+sensor + "/" + value
		print currentResource
	 
		r = requests.post(currentResource)
		print(r.text)