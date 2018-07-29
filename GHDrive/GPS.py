import time
import smbus
import math
import requests
from decimal import Decimal


class GPS:
	def __init__(self):
		self.bus = smbus.SMBus(1)
		self.address = 0x08
		self.lat = 0.0
		self.lon = 0.0
		self.prevObstacle = False

		
	def obstacleDetected(self):
		try:
			self.bus.write_byte(self.address, 0x03)
			time.sleep(.2)
			data = self.bus.read_byte(self.address)
			self.prevObstacle = (data == 0x02)
		except Exception:
			return self.prevObstacle
		return self.prevObstacle
	def degToRad(self, deg):
		return (deg*math.pi)/180
	def getHeading(self):
		'''
		self.bus.write_byte(self.address, 0x02)
		time.sleep(1)
		rawDat = self.dataToString(self.bus.read_i2c_block_data(self.address,0,6))
		try:
			return float(rawDat)
		except Exception:
			return self.calculateHeadingFromHistory()
		'''
		r = requests.get("http://ghp.novartech.net/locationUpdater.php")
		idx= r.text.find(":")
		return float(r.text[idx+1:])

	def calculateHeadingFromHistory(self):
		prevLat = self.lat
		prevLon = self.lon
		location = self.getLocation()
		deltaLat = prevLat-location[0]
		deltaLon = prevLon-location[1]
		if deltaLat == 0:
			if deltaLon<0:
				return 180.0
			else:
				return 0.0
		slope = deltaLat/deltaLon
		if deltaLat<0:
			if deltaLon<0:
				return math.atan(self.degToRad(slope))+360
			else:
				return math.atan(self.degToRad(slope))+180
		else:
			return math.atan(self.degToRad(slope))
	def calculateHeadingFromCoords(self, point1, point2):
		prevLat = point1[0]
		prevLon = point1[1]
		location = point2
		deltaLat = prevLat-location[0]
		deltaLon = prevLon-location[1]
		if deltaLat == 0:
			if deltaLon<0:
				return 180.0
			else:
				return 0.0
		slope = deltaLat/deltaLon
		if deltaLat<0:
			if deltaLon<0:
				return math.atan(self.degToRad(slope))+360
			else:
				return math.atan(self.degToRad(slope))+180
		else:
			return math.atan(self.degToRad(slope))
	def getLocation(self):
		'''
		self.bus.write_byte(self.address, 0x01)#set arduino to location command
		time.sleep(1)#wait for arduin to receive and process
		rawString = self.dataToString(self.bus.read_i2c_block_data(self.address, 0,30))
		splitIdx = rawString.find(",")
		#print(rawString[:(splitIdx-1)]+","+rawString[splitIdx+1:])
		try:
			llat = float(rawString[:(splitIdx-1)])
			llon = float(rawString[splitIdx+1:])
		except Exception:
			llat = 0.0
			llon = 0.0
		if llat+llon == 0.0:
			return [self.lat, self.lon]
		else:
			self.lat = llat
			self.lon = llon
			return [self.lat, self.lon]
		'''
		r = requests.get("http://ghp.novartech.net/locationUpdater.php")
		idx = r.text.find(":")
		idxComma = r.text.find(",")
		newStr = r.text[:idx-1]
		lat = newStr[:idxComma-1]
		lng = newStr[idxComma+1:]
		return [float(lat), float(lng)]
	
	def dataToString(self, blockData):
		str = ""
		for i in range(len(blockData)):
			if blockData[i] != 255:
				str += chr(blockData[i])
		return str