#! /usr/bin/python

import termios, fcntl, sys, os

sys.path.append('BAMPythonPublisher')
sys.path.append('BAMPythonPublisher/gen-py')

from Publisher import *
from BAMPublisher import *

import serial
import time
import datetime
import thread
import time

BAM_IP = 'localhost'					# IP address of the BAM/CEP server
BAM_PORT = 7713							# Thrift listen port of the server
BAM_UNAME = 'admin'						# Username to connect to server
BAM_PASSWRD = 'admin' 					# Passowrd to connect to server
BT_PORT = "/dev/ttyACM1"			# Port to which the Bluetooth Device is bound


bluetoothSerial = serial.Serial( BT_PORT, baudrate=9600 )

#while True:
#	print bluetoothSerial.readline()


publisher = Publisher()

# Initialize publisher with ip and port of server
publisher.init(BAM_IP, BAM_PORT)

# Connect to server with username and password
publisher.connect(BAM_UNAME, BAM_PASSWRD)

print "WAIT...."
# Define stream definition
streamDefinition = "{'name':'org_wso2_iot_statistics_device_pin_data','version':'1.0.0','nickName': 'IoT Connected Device Pin Data','description': 'Pin Data Received','tags': ['arduino', 'led13'],'metaData':[{'name':'ipAdd','type':'STRING'},      {'name':'deviceType','type':'STRING'},    {'name':'owner','type':'STRING'},{'name':'requestTime','type':'LONG'}],'payloadData':[ {'name':'macAddress','type':'STRING'}, {'name':'pin','type':'STRING'},{'name':'pinValue','type':'STRING'}, {'name':'description','type':'STRING'}]}";

publisher.defineStream(streamDefinition)
myPublisher = BAMPublisher()

class _Getch:
    def __call__(self):
		fd = sys.stdin.fileno()
		oldterm = termios.tcgetattr(fd)
		newattr = termios.tcgetattr(fd)
		newattr[3] = newattr[3] & ~termios.ICANON & ~termios.ECHO
		termios.tcsetattr(fd, termios.TCSANOW, newattr)
		try:
			ch1 = sys.stdin.read(1)
			if(ch1=='\x1b'):
				ch2 = sys.stdin.read(1)
				ch3 = sys.stdin.read(1)
				ch=ch1+ch2+ch3
			else: ch =ch1
			
		finally:
			termios.tcsetattr(fd, termios.TCSAFLUSH, oldterm)
			
		return ch

def get():
        inkey = _Getch()
        while(1):
                k=inkey()
                #print k
                if k==None:break
                if k!='':break
        if k=='\x1b[A':
                return 1
        elif k=='\x1b[B':
                return 2
        elif k=='\x1b[D':
                return 3
        elif k=='\x1b[C':
                return 4
        else:
                return 5;


def getControl( threadName, delay):
   	while(1):
		motion=get()
		print motion
		bluetoothSerial.write("{0}".format( motion))


def main():
#	print "Main Started";
	bluetoothSerial.write("6"); # simple approximate time sync-- assumed latency is negligible considering sensor information
	global time
	ts = time.time();
#	print "Reached 1";

	lines = bluetoothSerial.readline()
	print "Time Sync--"
	lines =""
	try:
		print "Started waiting for time sync"
		thread.start_new_thread( getControl, ("Thread", 2, ) )
		
	except:
		print "Error: unable to start thread"
	print "Robot Motion [Use Arrow Keys, Brake- Any other except arrows]"
	while True:
		lines = bluetoothSerial.readline()
#		print lines+"\n"
		sensorData=lines.split(',')
		for line in sensorData:
#			print line
#			if not(line.find("Time")):
#			print line
			line = line.split(':')
			sensor = line[0]
			value = line[1]
			time = ts+float(line[2])
			

			myPublisher.setPublishStream(time, sensor, value)
		# print myPublisher.metaData
		# print myPublisher.payloadData
	 
		# Publish message
			publisher.publish(myPublisher.metaData, myPublisher.payloadData)
	
	# Disconnect
	publisher.disconnect()
		

if __name__=='__main__':
        main()
