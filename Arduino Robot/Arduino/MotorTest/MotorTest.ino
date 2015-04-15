//motors
int motor_left[] = {2, 3};
int motor_right[] = {7, 8};

// --------------------------------------------------------------------------- Setup
void setup() {
Serial.begin(9600);

// Setup motors
int i;
for(i = 0; i < 2; i++){
pinMode(motor_left[i], OUTPUT);
pinMode(motor_right[i], OUTPUT);
}
}


void drive(){

}


// --------------------------------------------------------------------------- Loop
void loop() { 
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
             default : 
                      motor_stop();
                       break;
          
          }
    }

    
}

// --------------------------------------------------------------------------- Drive

void motor_stop(){
digitalWrite(motor_left[0], LOW); 
digitalWrite(motor_left[1], LOW); 

digitalWrite(motor_right[0], LOW); 
digitalWrite(motor_right[1], LOW);
delay(25);
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

void turn_left(){
motor_stop();
digitalWrite(motor_left[0], LOW); 
digitalWrite(motor_left[1], HIGH); 
delay(300);
motor_stop();

}

void turn_right(){
motor_stop();

digitalWrite(motor_right[0], LOW); 
digitalWrite(motor_right[1], HIGH); 
delay(300);
motor_stop();
}
