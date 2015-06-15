#include <pt.h> 
#define TURN_DELAY 100

int motion_global = 0;

int motor_left[] = {7, 8};
int enA = 12;

int motor_right[] = {6, 4};
int enB = 11;


void setup() {
  Serial.begin(115200);

  for(int i = 0; i < 2; i++){
    pinMode(motor_left[i], OUTPUT);
    pinMode(motor_right[i], OUTPUT);
  }
  
  pinMode(enA, OUTPUT);
  pinMode(enB, OUTPUT);
  
  digitalWrite(enA, 200);
  digitalWrite(enB,200);
  motor_stop();
}


void loop() {  
  drive();  
}


void drive(){
  if (Serial.available()) {
      motion_global=Serial.parseInt();
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
}










