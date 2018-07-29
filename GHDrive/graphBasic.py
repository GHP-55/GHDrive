## I was bored, and felt like writing some code.


##The nodes for on the path. Has 3 attributes, adjList, coords, and key.
##adjList is the adjacency list for this node
##coords is the coordinates this point corresponds to
##graph is the master graph that you are working with that has all the points
##
##Has a constructor that takes the coords and the graph and
##a toString function that prints its coordinates.

import math
import Utils
class PathNode:
    def __init__(self,coords,graph):
        self.weight = math.inf
        self.pathTo = []
        self.adjList = []
        self.coords = coords
        self.key = str(len(graph.nodeKeys))
        for node in graph.nodeKeys:
            if close(self,graph.nodes[node])==True:
                self.adjList.append(node)
                graph.nodes[node].adjList.append(self.key)
        graph.add(self)

    def toString(self):
        print("This is a path node, located at "+ str(self.coords))

##TODO
##This is the function that determines whether 2 nodes (points) are close enough
##to be connected. Do whatever you want here.
maxDistance = 28
def close(node1,node2):
	#return True
	return (distance(node1.coords, node2.coords)<=maxDistance)
	
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


##The Map class.
##Currently it tracks the nodes, and the keys to access those nodes (via dictionary)
## I did provide a toString function for you
##
## In addition, it has a function add(node), which you use to add a node to
## the map. You should never call this, but it is called when you create the
## node for the map (using the pathNode(coords,graph) constructor.)
##
##

class Map:

    def __init__(self):
        self.nodeKeys = []
        self.nodes = {}

    def getNodeByCoordinates(self, coordinates):
        for key in self.nodeKeys:
            if self.nodes[key].coords[0] == coordinates[0] and self.nodes[key].coords[1] == coordinates[1]:
                return self.nodes[key]
        return None

    def add(self,node):
        self.nodeKeys.append(node.key)
        if node.key not in self.nodes:
            self.nodes[node.key] = node
        else:
            print("ERROR! ABORT ABORT!\n")

    def save(self,fileName):
        #saves to a csv, or something
        return
    def load(self,fileName):
        #does something ith a fileName?
        return
    def toString(self):
        print("This is an entire map. It currently has "+ str(len(self.nodes)) + " nodes")
        for key in self.nodeKeys:
            print("The "+key+" node has coordinates "+str(self.nodes[key].coords))
            string = ""
            for node in self.nodes[key].adjList:
                string = string + str(self.nodes[node].key) + ","
            print("It is adjacent to the following nodes: " +string.strip(","))
    

##Some sample code:
berry = Map()
def init():
	for i in Utils.allCoords:
		PathNode((i[0],i[1]),berry)
    #this creates a node at coordinate (i,i^2) and adds it to the map
	#berry.toString()
    #berry.printCSVString()
    
    
