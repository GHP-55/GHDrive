'''
COMMANDS:
CMD		Name				Pin(Physical)		Desc
0		-									-
l		Left Wheel							Have to wait for esc specs
r		Right Wheel
u		Look Up/DOwn		11
p		Look Left/RIght		12
5		Send Range			
6		Send Location
n		Nav to Location

'''
import socket
import sys
import time
import RPi.GPIO as GPIO
import pigpio
import math
from _thread import *
import Navigator
import GPS
 
HOST = ''   # Symbolic name, meaning all available interfaces
PORT = 1738 # Arbitrary non-privileged port
PASSPHRASE = "gHp2k18"
#		udServo	lrServo
pins = [17,27,25, 24, 23, 18]

GPIO.setmode(GPIO.BCM)
#pigpio.start()
pi = pigpio.pi()
for pin in pins:
	GPIO.setup(pin, GPIO.OUT)
	pi.set_PWM_frequency(pin, 50)
	
#Setup Range Finder with Serial I guess
'''
upDownServo = PWM.Pin(pins[0], 50)
leftRightServo = PWM.Pin(pins[1], 50)
leftMotorFwd = PWM.Pin(pins[2], 50)
leftMotorBkd = PWM.Pin(pins[3], 50)
rightMotorFwd = PWM.Pin(pins[4], 50)
rightMotorBkd = PWM.Pin(pins[5], 50)
'''
upDownServo = pins[0]
leftRightServo = pins[1]
leftMotorFwd = pins[2]
leftMotorBkd = pins[3]
rightMotorFwd = pins[4]
rightMotorBkd = pins[5]
'''
upDownServo.start(7.5)
leftRightServo.start(7.5)
leftMotorBkd.start(0.0)
leftMotorFwd.start(0.0)
rightMotorBkd.start(0.0)
rightMotorFwd.start(0.0)
'''
 
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print('Socket created')
 
#Bind socket to local host and port
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
try:
	s.bind((HOST, PORT))
except socket.error as msg:
	print('Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1])
	sys.exit()
	 
print('Socket bind complete')
 
#Start listening on socket
s.listen(10)
print('Socket now listening')



global arrived
arrived = False
global locked
locked = True

def map(val, min1, max1, min2, max2):
	percent = val/(max1-min1)
	return ((max2-min2)*percent)+min2
def stop():
	pi.set_PWM_dutycycle(leftMotorBkd, 0)
	pi.set_PWM_dutycycle(leftMotorFwd, 0)
	pi.set_PWM_dutycycle(rightMotorBkd, 0)
	pi.set_PWM_dutycycle(rightMotorFwd, 0)
def goToCoordinates(lat, lon):
	gps = GPS.GPS()
	path = Navigator.generatePath(gps.getLocation(), [lat, lon])
	Navigator.dictPrint(path)
	#print(path)
	for node in reversed(path):
		print("Next Node Reached")
		driveToNode(node)
		while Navigator.distance(gps.getLocation(), node.coords)>5:
			time.sleep(.5)
		#When the loop exits, we have arrived at next point in path
		stop()


def driveToNode(node):
	gps = GPS.GPS()
	location = gps.getLocation()
	currentHeading = gps.getHeading()
	targetHeading = gps.calculateHeadingFromCoords(location, node.coords)
	delta = targetHeading-currentHeading
	if delta>180:
		delta = delta-360
	if delta<-180:
		delta = delta+360
	distance = Navigator.distance(location, node.coords)
	#I don't think this part will be relevant for our purposes, but it's cool math nonetheless
	radius = 12#In inches
	distanceToTravel = math.pi*(radius**2)
	distanceToTravel = distanceToTravel*(delta/720)
	#Could take average inches per milisecond at full throttle and calculate.
	#Problem is that this would probably vary with the charge of the battery
	if delta>0:
		stop()
		pi.set_PWM_dutycycle(leftMotorFwd, 255)
		pi.set_PWM_dutycycle(rightMotorBkd, 255)
	else:
		stop()
		pi.set_PWM_dutycycle(leftMotorBkd, 255)
		pi.set_PWM_dutycycle(rightMotorFwd, 255)
	time.sleep(abs(delta)/80)
	stop()
	if locked == False:
		pi.set_PWM_dutycycle(leftMotorFwd, 255)
		pi.set_PWM_dutycycle(rightMotorFwd, 255)
		
def degToPulse(degrees):
	if degrees == 0:
		degrees = 1
	return (degrees/(20/3))+7


def parseCommand(data):
	if locked == False:
		if data[0] == "u":
			pi.set_PWM_dutycycle(upDownServo, degToPulse(int(data[1:])))
			print("Move up/down", data[1:])
		elif data[0] == "p":
			pi.set_PWM_dutycycle(leftRightServo, degToPulse(int(data[1:])))
			print("Move left/right", data[1:])
		elif data[0] == "n":
			arrived = True
			time.sleep(1)
			arrived = False#Stop any current navigation
			coordStr = data[1:]
			lat = coordStr[0:coordStr.find(",")]
			lon = coordStr[coordStr.find(",")+1:]
			print("Navigating to: "+lat+" , "+lon)
			start_new_thread(goToCoordinates ,(float(lat),float(lon),))
		elif data[0] == "l":
			
			p = float(data[1:])

			#print("LP: "+str(p))
			if p<0.0:
				'''
				leftMotorBkd.ChangeDutyCycle(0.0)
				leftMotorFwd.ChangeDutyCycle(min(abs(power), 100.0))
				'''
				power = min(abs(p), 100)
				pi.set_PWM_dutycycle(leftMotorBkd, 0)
				pi.set_PWM_dutycycle(leftMotorFwd, map(power, 0, 100, 0, 255))
			else:
				'''
				leftMotorFwd.ChangeDutyCycle(0.0)
				leftMotorBkd.ChangeDutyCycle(min(abs(power), 100.0))
				'''
				power = min(abs(p), 100)
				pi.set_PWM_dutycycle(leftMotorFwd, 0)
				pi.set_PWM_dutycycle(leftMotorBkd, map(power, 0, 100, 0, 255))
		elif data[0] == "r":
			p = float(data[1:])
			#print("RP: "+str(p))
			if p<0.0:
				'''
				leftMotorBkd.ChangeDutyCycle(0.0)
				leftMotorFwd.ChangeDutyCycle(min(abs(power), 100.0))
				'''
				power = min(abs(p), 100)
				pi.set_PWM_dutycycle(rightMotorBkd, 0)
				pi.set_PWM_dutycycle(rightMotorFwd, map(power, 0, 100, 0, 255))
			else:
				'''
				leftMotorFwd.ChangeDutyCycle(0.0)
				leftMotorBkd.ChangeDutyCycle(min(abs(power), 100.0))
				'''
				power = min(abs(p), 100)
				pi.set_PWM_dutycycle(rightMotorFwd, 0)
				pi.set_PWM_dutycycle(rightMotorBkd, map(power, 0, 100, 0, 255))
		else:
			print(data[0])
	else:
		print("Locked, ignoring command")

def checkForObstacles():
	gps = GPS.GPS()
	global locked
	while True:

		locked = gps.obstacleDetected()
		
		if locked == True:
			stop()
			print("STOP!")
		time.sleep(.5)
		
#Function for handling connections. This will be used to create threads
def clientthread(conn):
	#Sending message to connected client
	conn.send('Please Enter Passphrase: \n'.encode('utf-8')) #send only takes string
	
	#Check passphrase
	data = conn.recv(7)
	if data.decode("utf-8") == PASSPHRASE:
		global locked
		locked = False
		conn.send("Access Granted".encode('utf-8'))
	else:
		conn.send("Incorrect Passphrase".encode('utf-8'))
	 
	#infinite loop so that function do not terminate and thread do not end.
	connected = True
	if connected and locked == False:
		start_new_thread(checkForObstacles, ())
		while connected:
		 
			#Receiving from client
			data = conn.recv(25)
			parseCommand(data.decode('utf-8'))#string
			if not data: 
				connected = False
	 
		#conn.sendall(reply)
	 
	#came out of loop
	conn.close()
	
def run():

	#now keep talking with the client
	while 1:
		#wait to accept a connection - blocking call
		conn, addr = s.accept()
		print('Connected with ' + addr[0] + ':' + str(addr[1]))
		start_new_thread(clientthread ,(conn,))
		
	s.close()

def start():
	start_new_thread(start, ())


