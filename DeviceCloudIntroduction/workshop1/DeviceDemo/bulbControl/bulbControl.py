#!/usr/bin/env python

import RPi.GPIO as GPIO
import time

# blinking function
def blink(pin):
       GPIO.output(pin,True)
       time.sleep(1)
       GPIO.output(pin,False)
       time.sleep(1)
       return

BULB_PIN = 11
# to use Raspberry Pi board pin numbers
# set up GPIO output channel
GPIO.setmode(GPIO.BOARD)
GPIO.setup(BULB_PIN, GPIO.OUT)

# blink GPIO17 50 times
for i in range(0,50):
       blink(BULB_PIN)

GPIO.cleanup()
