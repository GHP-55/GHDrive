import RPi.GPIO as GPIO
from _thread import *
import time

class Pin:
	
	running = True
	
	def sleep(self, delayMs):
		self.prevMilis =  int(round(time.time() * 1000))
		#print(str(time.time()-self.prevMilis))
		#print(str(delayMs))
		#print(str((time.time()-self.prevMilis)<delayMs))
		while ( int(round(time.time() * 1000))-self.prevMilis)<delayMs:
			continue
		return
	
	def togglePin(self, highTime, lowTime):
		while self.running == True:
			GPIO.output(self.pinNum, GPIO.HIGH)
			self.sleep(highTime)
			GPIO.output(self.pinNum, GPIO.LOW)
			self.sleep(lowTime)
		
	
	def __init__(self, pNum, freq):
		self.pinNum = pNum
		self.frequency = freq
		GPIO.setmode(GPIO.BCM)
		GPIO.setup(pNum, GPIO.OUT)
	def ChangeDutyCycle(self, percent):
		period = ((1/self.frequency)*1000)
		timeOn = (period*percent)/100
		timeOff = period-timeOn
		start_new_thread(self.togglePin, (timeOn, timeOff,))
	
	def stop(self):
		self.running = False