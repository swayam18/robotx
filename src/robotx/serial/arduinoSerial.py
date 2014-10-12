import serial
import SocketServer
import socket
import threading
import sys

class MyTCPHandler(SocketServer.StreamRequestHandler):
  def handle(self):
    print 'handling!'
    print ser
    while True:
      data = self.rfile.readline().strip()
      print data
      ser.write(data+"\n")

class ThreadedTCPServer(SocketServer.ThreadingMixIn, SocketServer.TCPServer):
    pass

ser = serial.Serial("/dev/ttyACM0")
if __name__ == "__main__":

  #connect to arduino
	#create server
	HOST, PORT = "localhost", 6665
	# Create the server, binding to localhost on port 12345
	#server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)
	server = ThreadedTCPServer((HOST, PORT), MyTCPHandler)
	
	# Start a thread with the server -- that thread will then start one
    # more thread for each request
	server_thread = threading.Thread(target=server.serve_forever)
	
	# Exit the server thread when the main thread terminates
	server_thread.daemon = True
	server_thread.start()
	print "Server Arduino Link Running"
	print "Server Address:", socket.gethostbyname(socket.gethostname()) 
	
	print "Connect to Arduino server on port:", PORT
	
	while True:
		command = raw_input("type 'quit' to exit program   ")
		print ""
	
		if command == 'quit':
			sys.exit()