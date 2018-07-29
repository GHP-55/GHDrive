import urllib.request

allCoords = []
masterListURL = "http://ghp.novartech.net/Master.csv"
def init():
	
	try:
		req = urllib.request.urlopen(masterListURL)
		csvFile = open("Master.csv", "w")
		for line in req.readlines():
			#print (line.decode("utf-8"))
			idx = line.find(",".encode("utf-8"))
			lat = float(line[0:idx])
			lon = float(line[idx+1:])
			coord = [lat, lon]
			csvFile.write(str(lat))
			csvFile.write(",")
			csvFile.write(str(lon))
			csvFile.write("\n")
			allCoords.append(coord)
	except Exception as e:
		print("Error updating master list")
		csvFile = open("Master.csv", "r")
		for line in csvFile.readlines():
			#print (line.decode("utf-8"))
			idx = line.find(",")
			try:
				coord = [float(line[0:idx]), float(line[idx+1:])]
				allCoords.append(coord)
			except Exception as e:
				print()