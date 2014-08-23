robotx
======

Installation and Running
===
First, clone the repository and cd into it.
Then cd to src and run ./build.sh

* Sensors Setup
===
Run the following in terminal:

sudo gpsd -N /dev/ttyACM0 /dev/ttyUSB0

ttyACM0 is the path of the gps device and ttyUSB0 is the pass of the compass device.
