#include <avr/wdt.h>
#include <Adafruit_CC3000.h>
#include <SPI.h>
#include "utility/debug.h"
#include "utility/socket.h"
#include "ctype.h"

// These are the interrupt and control pins
#define ADAFRUIT_CC3000_IRQ   3  // MUST be an interrupt pin!
// These can be any two pins
#define ADAFRUIT_CC3000_VBAT  5
#define ADAFRUIT_CC3000_CS    10
// Use hardware SPI for the remaining pins
// On an UNO, SCK = 13, MISO = 12, and MOSI = 11

Adafruit_CC3000 cc3000 = Adafruit_CC3000(ADAFRUIT_CC3000_CS, ADAFRUIT_CC3000_IRQ, ADAFRUIT_CC3000_VBAT,
                                         SPI_CLOCK_DIVIDER); // you can change this clock speed

#define TURN_DELAY 100

int motion_global = 0;

//motors
int motor_left[] = {9, 11};
int motor_right[] = {7, 8};

int enA=12;
int enB=13;
//byte mac[6] = { 0xC0, 0x4A, 0x00, 0x1A, 0x03, 0xF8 };



void setup() {
  Serial.begin(115200);

  int i;
  for(i = 0; i < 2; i++){
    pinMode(motor_left[i], OUTPUT);
    pinMode(motor_right[i], OUTPUT);
  }
  pinMode(enA, OUTPUT);
  pinMode(enB, OUTPUT);
  digitalWrite(enA, 100);
  digitalWrite(enB, 100);
  
  //motor_stop();
  initializeServer();
 
 //watchdog
  wdt_enable(WDTO_4S);
}

void loop() {  
 listen();
 wdt_reset();
  
  // Check connection
  if(!cc3000.checkConnected()){while(1){}}
  wdt_reset(); 

 drive();
 

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

void drive(){
  
          
          switch(motion_global){
            
              //Serial.println("########"+String(motion_global));
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


void updateDirectionVariable(int direction){
  motion_global = direction;
}










