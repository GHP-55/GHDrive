import Utils
import graphBasic
import Navigator

Utils.init()
graphBasic.init()

while True:
	startStr = input("Start Coordinates (lat,lon): ")
	destStr = input("Destination Coordinates: ")
	idx1 = startStr.find(",")
	idx2 = destStr.find(",")
	lat1 = float(startStr[:idx1-1])
	lon1 = float(startStr[idx1+1:])
	lat2 = float(destStr[:idx2-1])
	lon2 = float(destStr[idx2+1:])
	Navigator.generatePath([lat1, lon1], [lat2, lon2])