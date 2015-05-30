#include <pt.h> 
#define TURN_DELAY 100

int motion_global = 0;

//motors
int motor_right[] = {9, 11};
int motor_left[] = {7, 8};

int enA=12;
int enB=13;


//thread init
static struct pt pt2;


void setup() {
  Serial.begin(115200);
  PT_INIT(&pt2);
  
  int i;
  for(i = 0; i < 2; i++){
    pinMode(motor_left[i], OUTPUT);
    pinMode(motor_right[i], OUTPUT);
  }
  pinMode(enA, OUTPUT);
  pinMode(enB, OUTPUT);
  digitalWrite(enA, 200);
  digitalWrite(enB, 200);
  
  motor_stop();
  initializeServer();

}

void loop() {  
 listen();
// protothread2(&pt2, 1000);
 drive();
 //drive_forward();
 //Serial.println("sdf"+String(motion_global));
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
  
          
          switch(motion_global){
            
              Serial.println("########"+String(motion_global));
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

static int protothread2(struct pt *pt, int interval) {
  static unsigned long timestamp = 0;
  PT_BEGIN(pt);
  while(1) { // never stop 
    /* each time the function is called the second boolean
    *  argument "millis() - timestamp > interval" is re-evaluated
    *  and if false the function exits after that. */
    PT_WAIT_UNTIL(pt, millis() - timestamp > interval );
    timestamp = millis(); // take a new timestamp
    
    PT_END(pt);
  }
}

void updateDirectionVariable(int direction){
  motion_global = direction;
}









