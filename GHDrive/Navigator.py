import math
import Utils
from _thread import *
import graphBasic
import queue
import time


def generatePath(start, dest):
	nodes = graphBasic.berry.nodes
	start = nearestNode(start, nodes)
	dest = nearestNode(dest, nodes)
	AdjNodes = {}#weights
	Visited = {}#weights of nodes
	Previous = {}
	for node in nodes:
		AdjNodes[node] = math.inf
	Visited[start.key] = 0
	AdjNodes.pop(start.key)
	currNode = start.key
	print("Starting at node "+currNode+", destination is node "+dest.key)
	while currNode != dest.key:
		#print("CurrNode: "+currNode)
		for node in nodes[currNode].adjList:
			#print("Node: "+node)
			#print("Visited: "+str(Visited))
			#print("Adjacent: "+str(nodes[currNode].adjList))
			if node not in Visited:
				d = Visited[currNode]+graphBasic.distance(nodes[currNode].coords, nodes[node].coords)
				if d<AdjNodes[node]:
					AdjNodes[node] = d
					Previous[node] = nodes[currNode]
		nextKey = min(AdjNodes, key = AdjNodes.get)
		#print("About to visit node "+nextKey)
		Visited[nextKey] = AdjNodes[nextKey] 
		AdjNodes.pop(nextKey)
		currNode = nextKey
		#time.sleep(1)
	#printUnarranged(Previous)
	path = []
	pathVisited = []
	currNode = dest.key
	while currNode != start.key:
		minDistance = math.inf
		minNode = None
		#print(nodes[currNode].adjList)
		for node in nodes[currNode].adjList:
			if node not in pathVisited:
				#print("Visiting node "+node)
				pathVisited.append(node)
				if node in Visited:
					d = Visited[node]
					#print("Weight is "+str(d))
					if d<minDistance:
						#print("New min node is "+node)
						minDistance = d
						minNode = node
		path.append(nodes[minNode])
		#print(minNode)
		currNode = minNode
	dictPrint(path)
	print("Distance is: "+str(Visited[dest.key]) + " feet")
	#return path
def nearestNode(coords, nodes):
	nearest = None
	nearestDist = math.inf
	for k,coor in nodes.items():
		dist = graphBasic.distance(coor.coords, coords)
		if dist<nearestDist:
			nearest = coor
			nearestDist = dist
	return nearest
def printUnarranged(dict):
	for k,v in dict.items():
		print(str(v.coords[0])+","+str(v.coords[1])+","+k)
def dictPrint(dict):
	for v in dict:
		print(str(v.coords[0])+","+str(v.coords[1]))
def bsf(start, dest):
	map = graphBasic.berry
	queue = []
	visited = []
	currentNode = map.getNodeByCoordinates(start)
	currentNode.weight = 0
	queue.append(currentNode)
	while len(queue)>0:
		currentNode = queue.pop(0)#Really parent node
		for key in currentNode.adjList:
			node = map.nodes[key]
			if node not in visited:
				node.weight = currentNode.weight+distance(currentNode.coords, node.coords)
				
				node.pathTo = currentNode.pathTo#wikipedia calls this action I think
				node.pathTo.append(currentNode)
				if node.coords[0] == dest[0] and node.coords[1] == dest[1]:
					return node.pathTo
				visited.append(node)#Really grey area in graph I guess
				queue.append(node)
	#print(str(len(visited)))

	
def numIsBetween(num, boundOne, boundTwo):
	if boundOne<boundTwo:
		return (num<boundTwo and num>boundOne)
	else:
		return (num>boundTwo and num<boundOne)
maxDistance = 28	
def distance(coord1, coord2):
	if len(coord1)>0 and len(coord2)>0:
		R = 20925721.785

		lat1 = math.radians(coord1[0])
		lon1 = math.radians(coord1[1])
		lat2 = math.radians(coord2[0])
		lon2 = math.radians(coord2[1])

		dlon = lon2 - lon1
		dlat = lat2 - lat1

		a = math.sin(dlat / 2)**2 + math.cos(lat1) * math.cos(lat2) * math.sin(dlon / 2)**2
		c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))

		dist = R * c
		return dist
	else:
		return math.inf

def nearestCoord(coords, possibleCoords):
	if len(possibleCoords)>0:
		nearest = possibleCoords[0]
		nearestDist = distance(coords, nearest)
		for coor in possibleCoords:
			dist = distance(coor, coords)
			if dist<nearestDist:
				nearest = coor
				nearestDist = dist
		if distance(nearest, coords)<=maxDistance:
			return nearest
		else:
			return None
	else:
		return None
	
