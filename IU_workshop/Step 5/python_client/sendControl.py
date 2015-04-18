#! /usr/bin/python
import serial
import time
bluetoothSerial = serial.Serial( "/dev/ttyACM1", baudrate=9600 )


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

def main():
	
	while(1):
		motion=get()
		print motion
		bluetoothSerial.write("{0}".format( motion))
		

if __name__=='__main__':
        main()
