#define TURN_DELAY 100

//Motor Pin configurations
int motor_left[] = {3, 2};
int motor_right[] = {8, 7};

void setup() {
Serial.begin(9600);
int i;
for(i = 0; i < 2; i++){
pinMode(motor_left[i], OUTPUT);
pinMode(motor_right[i], OUTPUT);
}
}

void loop() { 
//  if (Serial.available()) {
//    Serial.write("Serial is available!!!!");
//  }
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
                      Serial.write(" GOING FORWARD ");
                      drive_forward();
                       break;
             case 2 : drive_backward();
                     Serial.write(" GOING BACK ");
                       break;
             case 3 : turn_left();
                      Serial.write(" GOING LEFT");
                       break;
             case 4 : turn_right();
                      Serial.write(" GOING RIGHT");
                       break;
             default : 
                      motor_stop();
                       break;     
          }
    }
}






