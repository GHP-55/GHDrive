import requests
import socket
import time


s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.connect(("8.8.8.8",80))
ip = s.getsockname()[0]
murl = "http://ghp.novartech.net/ipGiver.php?ip="+str(time.time())+":"+ip
headers = {"User-Agent": "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0"}
try:
	r = requests.get(url = murl, params = headers)
	print(r.text)
except requests.exceptions.ConnectionError:
	#r.status_code = "Connection refused"
	print("Conn refused")
