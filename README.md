Robotx
======

Robotx autonomous marine navigation system

Installation and Running
---------------------
First, clone the repository and cd into it.

Now we need to start judge server.
To do that, run: python src/robotx/judge/judge.py

Now we need to start arduino server.
To do that, run: python src/robotx/serial/arduinolink.py

Connect Compass and Gps, and only then connect arduino.

Then cd to src and run sudo ./run.sh
You need the sudo if arduino is connected. If you only want to compile the files, run ./build.sh

### Libraries

You need RXTX library installed for arduino communication. Use this:
http://www.jcontrol.org/download/readme_rxtx_en.html

### Sensors Setup
Make sure you attach the arduino in the end.

Run the following in terminal:

sudo gpsd -D 2 -N /dev/ttyACM0 /dev/ttyUSB0

ttyACM0 is the path of the gps device and ttyUSB0 is the pass of the compass device.

### Useful tips

Use dmesg | grep 'tty' to figure out the ports of the various devices
