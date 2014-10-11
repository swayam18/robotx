/*

Author: Jaron Lee Jia Wen
Date last modified: 4:30PM, 8/10/2014

Arduino pin configuration
Pin 5: Failsafe pin (transit between Serial and TX/RX transmitter)
Pin 7: Throttle pin
Pin 8: Servo 1 pin
Pin 9: Servo 2 pin
Pin 11: Ignition Pin

Limits of pin 5 and 11 on the Futaba T14SG
HIGH: 1093
MID: 1513
LOW: 1926

 */

#include <Servo.h> 

Servo myservo1;  // create servo object to control a servo int throttlePin = 7;
Servo myservo2; // another servo

int throttlePin = 7; // Throttle stick of the TX connected to pin 7 of the arduino
int failsafe = 5; // failsafe pin
int ignitionPin = 11; //ignition pin
int ignitionValue; // ignition value
int throttleValue; // throttle pin value
int servoValue;  // servo angle value
int incomingValue; // serial read value
int failsafeValue; // failsafe pulse in value
boolean triggerFlag = false;

void setup(){
  pinMode(throttlePin, INPUT);
  pinMode(failsafe, INPUT);
  pinMode(ignitionPin, INPUT);
  Serial.begin(9600); // declare serial communications
  myservo1.attach(8); // servo attached to pin 8
  myservo2.attach(9); // servo attached to pin 9
}


// Codes for reading serial data from computer
void serialData(){
  if(Serial.available() > 0){ // check for incoming serial data
    // look for the next valid integer in the incoming serial stream:
    int lservo = Serial.parseInt(); 
    // do it again:
    int rservo = Serial.parseInt(); 
    // do it again:

    // look for the newline. That's the end of your
    // sentence:
    if (Serial.read() == '\n') {
      myservo1.write(lservo);
      myservo2.write(rservo);
    }
  } // end of Serial Data



// codes for reading Pulses on the TX controller
void throttleStick(){

  throttleValue = pulseIn(throttlePin,HIGH); // reading the HIGH pulses
  Serial.print(throttleValue);
  Serial.println(" ");


  // Mapping throttle position on to servo control
  servoValue = map(throttleValue, 1093, 1926, 0, 180); // mapping of pulses onto Servo movement
  myservo1.write(servoValue); // writing values on the servo
  myservo2.write(servoValue);
}

// end of throttle stick command

void readIgnitionValues(){
  ignitionValue = pulseIn(ignitionPin,HIGH);
  Serial.print("ignition Pin pulse in value is: ");
  Serial.print(ignitionValue);
  Serial.println(" ");
}  


void loop(){
  serialData(); //reading ignition pin
  // 120,234
  delay(100);
}// end of void loop
