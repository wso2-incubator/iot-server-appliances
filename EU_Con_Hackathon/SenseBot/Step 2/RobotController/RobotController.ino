//#include <avr/wdt.h>
#include <Adafruit_CC3000.h>
#include <SPI.h>

// These are the interrupt and control pins
#define ADAFRUIT_CC3000_IRQ   3  // MUST be an interrupt pin!
// These can be any two pins
#define ADAFRUIT_CC3000_VBAT  5
#define ADAFRUIT_CC3000_CS    10

Adafruit_CC3000 cc3000 = Adafruit_CC3000(ADAFRUIT_CC3000_CS, ADAFRUIT_CC3000_IRQ, ADAFRUIT_CC3000_VBAT,
                                         SPI_CLOCK_DIVIDER); // you can change this clock speed

#define TURN_DELAY 100

int motion_global = 0;

int motor_left[] = {6,4};
int enA = 12;

int motor_right[] = {8, 7};
int enB = 11;

void setup() {
  Serial.begin(115200);

  for(int i = 0; i < 2; i++){
    pinMode(motor_left[i], OUTPUT);
    pinMode(motor_right[i], OUTPUT);
  }
  
  pinMode(enA, OUTPUT);
  pinMode(enB, OUTPUT);
  digitalWrite(enA, 30);
  digitalWrite(enB, 30);
  
  motor_stop();
  initializeServer();
 
 //watchdog
//  wdt_enable(WDTO_4S);
}



void loop() {  
 listen();
// wdt_reset();
//  // Check connection
//  if(!cc3000.checkConnected()){
//    while(1){}
//  }
//  wdt_reset(); 

 drive();
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
