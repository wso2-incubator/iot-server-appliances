#include "EUHackathonRobot.h"

#include <Adafruit_CC3000.h>
#include <avr/wdt.h>
#include <SPI.h>
#include "dht.h"
#include <pt.h>

Adafruit_CC3000 cc3000 = Adafruit_CC3000(ADAFRUIT_CC3000_CS, ADAFRUIT_CC3000_IRQ, ADAFRUIT_CC3000_VBAT,
                                         SPI_CLOCK_DIVIDER); // you can change this clock speed

Adafruit_CC3000_Client pushClient;
Adafruit_CC3000_Server httpServer(LISTEN_PORT);

static struct pt pushThread;

int motor_right[] = {9, 11};
int motor_left[] = {7, 8};
int enA = 12;
int enB = 13;

int motion_global = 0;
static unsigned long previous = 0;
static int gap = 0;

    /**********************************************************************************************  
        0. Check with a sample Wifi code of the Adafruit_CC3000 library to ensure that the sheild is working
        1. Set the ip of the server(byte array below) where the Web-Rest API for the FireAlarm is running
        2. Check whether the "SERVICE_EPOINT" is correct in the 'EUHackothonRobot.h.h' file
        3. Check whether the "SERVICE_PORT" is the same (9763) for the server running. Change it if needed
        4. Check whether the pins have been attached accordingly in the Arduino
        5. Check whether all reqquired pins are added to the 'digitalPins' array  
    ***********************************************************************************************/

uint32_t sserver;
byte server[4] = { 10, 100, 7, 38 };

String host, jsonPayLoad;
dht DHT;
  


void setup() {
  Serial.begin(115200);
  pinMode(PIR_PIN, INPUT);
  
  for(int i = 0; i < 2; i++){
    pinMode(motor_left[i], OUTPUT);
    pinMode(motor_right[i], OUTPUT);
  }
  
  motor_stop();
  
  pinMode(enA, OUTPUT);
  pinMode(enB, OUTPUT);
  digitalWrite(enA, 100);
  digitalWrite(enB, 100);
  
  PT_INIT(&pushThread);
  
  connectHttp();
  setupResource();
  wdt_enable(WDTO_4S);
}



void loop() {
  
  protothread1(&pushThread, 1000);
  
//  listen();
  wdt_reset();
  
  // Check connection
  if( !cc3000.checkConnected() ){
    while(1){
    }
  }
  
  wdt_reset(); 

  drive();
  
  
  
  
//  if (millis() - previous > gap) {
//    if(motion_global==0){
//      motion_global=1;
//      drive_forward();
//      gap=10000;
//    } else if (motion_global==1) {
//      motion_global=2;
//      motor_stop();
//      gap=5000;
//    } else if (motion_global==2) {
//      motion_global=0;
//      drive_backward();
//      gap=5000;
//    }
//   
//    previous=millis(); 
//  }   
}


void drive(){   
  switch(motion_global){           
     case 1 : drive_forward();                     
               break;
     case 2 : drive_backward();            
               break;
     case 3 : turn_left();           
               break;
     case 4 : turn_right();
               break;                   
     case 5 : 
              motor_stop();          
               break;  
  }
}


void updateDirectionVariable(int motionDir){
  motion_global = motionDir;
}



static int protothread1(struct pt *pt, int interval) {
  static unsigned long timestamp = 0;
  PT_BEGIN(pt);
  while(1) { // never stop 
    /* each time the function is called the second boolean
    *  argument "millis() - timestamp > interval" is re-evaluated
    *  and if false the function exits after that. */
    PT_WAIT_UNTIL(pt, listen() );

    if (pushClient.connected()) {   
                       // batches all the required pin values together and pushes once
         // Pushes data in 1 second interval
      pushData(); 
      wdt_reset();  
    } else {
      pushClient.close();
      cc3000.disconnect();
      connectHttp(); 
    }   
  }
  PT_END(pt);
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
  //motor_stop();
  digitalWrite(motor_left[0], LOW); 
  digitalWrite(motor_left[1], HIGH); 
  
  digitalWrite(motor_right[0], LOW); 
  digitalWrite(motor_right[1], HIGH); 
}

void drive_forward(){
  //motor_stop();
  digitalWrite(motor_left[0], HIGH); 
  digitalWrite(motor_left[1], LOW); 
  
  digitalWrite(motor_right[0], HIGH); 
  digitalWrite(motor_right[1], LOW); 
}


void turn_right(){
  motor_stop();
  digitalWrite(motor_left[0], LOW); 
  digitalWrite(motor_left[1], HIGH); 
  unsigned long  motorStop= millis() + TURN_DELAY;  
  while (!(motorStop<= millis())){
  //delay 300ms
  }
  updateDirectionVariable(0);
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
  updateDirectionVariable(0);
  motor_stop();
}



