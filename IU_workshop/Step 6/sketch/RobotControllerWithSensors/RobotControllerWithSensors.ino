#include "MultipleSensors.h"
#include <dht.h>
#include <pt.h> 
#define TURN_DELAY 100
 

//motors
int motor_left[] = {3, 2};
int motor_right[] = {8, 7};

//sensors
//dht DHT;
int motionDetect, lightLevel, gasValue;
double temperature;
long sonar;


//thread init
static struct pt pt1;
float startTime=0;
bool transmit=false;

void setup() {
  Serial.begin(9600);
    pinMode(PIR_PIN, INPUT);
  PT_INIT(&pt1);
  int i;
for(i = 0; i < 2; i++){
pinMode(motor_left[i], OUTPUT);
pinMode(motor_right[i], OUTPUT);
}
}

void loop() { 
  
  drive();
  protothread1(&pt1, 1000);
  
}





void motor_stop(){
digitalWrite(motor_left[0], LOW); 
digitalWrite(motor_left[1], LOW); 

digitalWrite(motor_right[0], LOW); 
digitalWrite(motor_right[1], LOW);
unsigned long  motorStop= millis() + 25;  
while (!(motorStop<= millis())){
//delay 25ms
}
}

void drive_backward(){
motor_stop();
digitalWrite(motor_left[0], HIGH); 
digitalWrite(motor_left[1], LOW); 

digitalWrite(motor_right[0], HIGH); 
digitalWrite(motor_right[1], LOW); 
}

void drive_forward(){
motor_stop();
digitalWrite(motor_left[0], LOW); 
digitalWrite(motor_left[1], HIGH); 

digitalWrite(motor_right[0], LOW); 
digitalWrite(motor_right[1], HIGH); 
}

void turn_right(){
motor_stop();
digitalWrite(motor_left[0], LOW); 
digitalWrite(motor_left[1], HIGH); 
unsigned long  motorStop= millis() + TURN_DELAY;  
while (!(motorStop<= millis())){
//delay 300ms
}
motor_stop();

}

void turn_left(){
motor_stop();

digitalWrite(motor_right[0], LOW); 
digitalWrite(motor_right[1], HIGH); 
unsigned long  motorStop= millis() + TURN_DELAY;  
while (!(motorStop<= millis())){
//delay 300ms
}
motor_stop();
}

void drive(){
if (Serial.available()) {
          int motion = Serial.parseInt();
          
          switch(motion){
             case 1 : 
                      drive_forward();
                      
                       break;
             case 2 : drive_backward();
                      
                       break;
             case 3 : turn_left();
                      
                       break;
             case 4 : turn_right();
                  
                       break;
             case 6 : timeInit();
                  
                       break;
                       
             default : 
                      motor_stop();
                       break;
          
          }
    }

}

void timeInit(){
  startTime=millis();
  Serial.print("Time Sync - Tranmit started!");
  transmit=true;
}


static int protothread1(struct pt *pt, int interval) {
  static unsigned long timestamp = 0;
  PT_BEGIN(pt);
  while(1) { // never stop 
    /* each time the function is called the second boolean
    *  argument "millis() - timestamp > interval" is re-evaluated
    *  and if false the function exits after that. */
    PT_WAIT_UNTIL(pt, millis() - timestamp > interval );
    timestamp = millis(); // take a new timestamp
    if(transmit){
      getSonar();
//      motionSense();
//      lightSense();
//      getTemperature();
    }
  }
  PT_END(pt);
}






