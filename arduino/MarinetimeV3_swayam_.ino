/*

Author: Jaron Lee Jia Wen
Date last modified: 1:30PM, 15/10/2014

Arduino pin configuration
Pin 3: Left Throttle Pin (pin 3 on the receiver)
Pin 5: Failsafe pin (transit between Serial and TX/RX transmitter)
Pin 7: Right Throttle pin (pin 2 on receiver)
Pin 8: Servo 1 pin
Pin 9: Servo 2 pin
Pin 11: Ignition Pin (Pin 6 on the receiver)
Pin 10: Relay Fail Safe

Limits of pin 5 and 11 on the Futaba T14SG
HIGH: 1093
MID: 1513
LOW: 1926

*/

#include <Servo.h> 
 
  Servo leftServo;  // create servo object to control a servo int throttlePin = 7;
  Servo rightServo; // another servo

  int rightThrottlePin = 7; // Right Throttle stick of the RX  pin 2 connected to pin 7 of the arduino
  int leftThrottlePin = 3; // Left Throttle sitck of the RX pin 3 connected to pin 3 of the arduino
  int failsafe = 5; // failsafe pin
  int ignitionPin = 11; //ignition pin
  int ignitionValue; // ignition value
  int rightThrottleValue; // throttle pin value
  int leftThrottleValue;
  int rightServoValue;  // servo angle value
  int leftServoValue;
  int failsafeValue; // failsafe pulse in value
  boolean triggerFlag = false;
  int leftServoData;
  int rightServoData;
  int relayFailSafe = 10; // Pin 10 on the arduino reads the Pin 7 of the receiver
  int relaySwitch = 22;
  int toggleSwitch;

  void setup(){
  pinMode(leftThrottlePin, INPUT);
  pinMode(rightThrottlePin, INPUT);
  pinMode(failsafe, INPUT);
  pinMode(ignitionPin, INPUT);
  pinMode(relayFailSafe, INPUT);
  pinMode(relaySwitch, OUTPUT);
  Serial.begin(9600); // declare serial communications
  leftServo.attach(8); // servo attached to pin 8
  rightServo.attach(9); // servo attached to pin 9
}


// Codes for reading serial data from computer
  void serialData(){
  if(Serial.available() > 0){ // check for incoming serial data
 
  // look for the next valid integer in the incoming serial stream
  if(Serial.read() != '$') return;
  leftServoData = Serial.parseInt(); // read the data stream
  rightServoData = Serial.parseInt(); // read the data stream
  
    // print information  
    Serial.print("Left Servo Data is: ");
    Serial.print(leftServoData);
    Serial.println(" ");
       
    Serial.print("Right Servo Data is: ");
    Serial.print(rightServoData);
    Serial.println(" ");
       
    // write data to the servos
    char c = Serial.read();
    if (c == '\n'){
      Serial.println("got newline. sending data");
      leftServo.write(leftServoData);
      rightServo.write(rightServoData); 
    }    

    else {
      Serial.println("Didnt get newline!, got instead");
      Serial.print(c);
      Serial.println("  <--------");
    }
             
  } // end of serial available
    } // end of Void Serial Data



// codes for reading Pulses on the TX controller
  void throttleStick(){
       
  rightThrottleValue = pulseIn(rightThrottlePin,HIGH); // reading the HIGH pulses
  leftThrottleValue = pulseIn(leftThrottlePin,HIGH);
  
  // Mapping throttle position on to servo control
  
  // Right Throttle Control
  rightServoValue = map(rightThrottleValue, 1093, 1926, 0, 180); // mapping of pulses onto Servo movement
  rightServo.write(rightServoValue);
  
  // Left Throttle Control
  leftServoValue = map(leftThrottleValue, 1093, 1926, 0, 180);
  leftServo.write(leftServoValue); // writing values on the servo
  
  }

 // end of throttle stick command
  
  void readIgnitionValues(){
  ignitionValue = pulseIn(ignitionPin,HIGH);
}  
  
  void relayFailSafeCommand(){
    toggleSwitch = pulseIn(relayFailSafe,HIGH);
    if (toggleSwitch < 1500){
      digitalWrite(relaySwitch,HIGH);
    }
    else{
      digitalWrite(relaySwitch,LOW);
    }
  } // end of relayFailSafeCommand

  
  
  

  void loop(){
  
    relayFailSafeCommand(); // read the relay switch command
    readIgnitionValues(); //reading ignition pin
    
  
  if (ignitionValue<=1095){ // if the ignition pin is at high position value, do nothing 
  leftServo.write(90); // even if its in the idle mode. the motor position should be at rest thus the servo is writing at 90 degrees
  rightServo.write(90);
  triggerFlag = false;
    }
  
  else if (ignitionValue>= 1450 && ignitionValue<=1550){ // ignition at mid position value, IDLE mode
    leftServo.write(90); // motor at idle/ rest mode. Servo at 90.
    rightServo.write(90);
    triggerFlag=false;    
    Serial.print("ignition Pin pulse is in the middle. Servo at 90");  
  }
   
   else{        // ignition pin at low position value
  Serial.print("ignition Pin pulse is LOW. Normal mode activated");
  triggerFlag = true;  
   }


  // Trigger Flag mode is activated (TRUE) (MANUAL AND AUTONOMOUS MODE
  
  if(triggerFlag){ // trigger flag toggles the failsafe feature between manual control and serial data control
  failsafeValue = pulseIn(failsafe,HIGH);

  Serial.print("Fail safe pulse in value is: ");
  Serial.print(failsafeValue);
  Serial.println(" ");
  
  if (failsafeValue < 1100){ // activating the fail safe command with the flick of switch 5
    serialData();  // if the switch is unflicked it will resume at the default serial communication mode
    Serial.print("AUTONOMOUS MODE");
    Serial.println(" ");    
  }
  
  else{
    Serial.flush();
    throttleStick();  // if the switch is flicked the fail safe command will take over and use the TX controller to provide manual inputs
    Serial.print("MANUAL OVERRIDE");
    Serial.println(" ");
    } 
   } // end of trigger Flag
  }// end of void loop
   

  

