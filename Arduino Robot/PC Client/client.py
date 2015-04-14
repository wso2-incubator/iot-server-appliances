#! /usr/bin/python
import serial
import time
import requests
import datetime
import thread
import time

bluetoothSerial = serial.Serial( "/dev/tty.HC-06-DevB", baudrate=9600 )


serverIP = "localhost"
serverPort = "9763"
publisherEndpoint = "/ConnectedDevices-1.0.0/pushdata"

#"/pushdata/{ip}/{owner}/{type}/{mac}/{time}/{pin}/{value}")
deviceIP = "/192.168.1.999"
deviceOwner = "/SMEAN"
deviceType = "/ArduinoUNO"
deviceMAC = "/98:D3:31:80:38:D3"

publisherEndpoint = "http://" + serverIP + ":" + serverPort + publisherEndpoint + deviceIP + deviceOwner + deviceType + deviceMAC + "/"



import termios, fcntl, sys, os
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

	bluetoothSerial.write("6"); # simple approximate time sync-- assumed latency is negligible considering sensor information
	global time
	ts = time.time()
	lines = bluetoothSerial.readline()
	print lines
	try:
		print "Started waiting for time sync"
		thread.start_new_thread( getControl, ("Thread", 2, ) )
		
	except:
		print "Error: unable to start thread"

	while True:
		lines = bluetoothSerial.readline()
		#print lines+"\n"
		sensorData=lines.split(',')
		for line in sensorData:
			line = line.split(':')
			sensor = line[0]
			value = line[1]
			time = ts+float(line[2])
			

			currentResource = publisherEndpoint + str(long(round(time)))+"/"+sensor + "/" + value
			#print currentResource
		 
			r = requests.post(currentResource)
			#print(r.text)
	
	
		

if __name__=='__main__':
        main()
