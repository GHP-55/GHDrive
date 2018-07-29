import Utils
import Navigator
#Linked List
class Node(object):
	location = None
	adjList = []
	pathTo = []
	
	def __init__(self, lat, lon):
		location = (lat, lon)
		for i in Utils.allCoords:
			if Navigator.distance(location, i)<Navigator.maxDistance and i != location:
				adjList.append()