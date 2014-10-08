Robotx
======

Robotx autonomous marine navigation system

Installation and Running
---------------------
First, clone the repository and cd into it.
Then cd to src and run ./build.sh

### Libraries

You need RXTX library installed for arduino communication. Use this:
http://www.jcontrol.org/download/readme_rxtx_en.html

### Sensors Setup

Run the following in terminal:

sudo gpsd -N /dev/ttyACM0 /dev/ttyUSB0

ttyACM0 is the path of the gps device and ttyUSB0 is the pass of the compass device.
