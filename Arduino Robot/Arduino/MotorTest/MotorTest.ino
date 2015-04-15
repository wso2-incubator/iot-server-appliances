//motors
int motor_left[] = {2, 3};
int motor_right[] = {7, 8};

// --------------------------------------------------------------------------- Setup
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

// --------------------------------------------------------------------------- Loop
void loop() { 

drive_forward();
delay(4000);
motor_stop();
Serial.println("1");

drive_backward();
delay(1000);
motor_stop();
Serial.println("2");

turn_left();
delay(1000);
motor_stop();
Serial.println("3");

turn_right();
delay(1000);
motor_stop();
Serial.println("4"); 

motor_stop();
delay(1000);
motor_stop();
Serial.println("5");
}

// --------------------------------------------------------------------------- Drive

void motor_stop(){
digitalWrite(motor_left[0], LOW); 
digitalWrite(motor_left[1], LOW); 

digitalWrite(motor_right[0], LOW); 
digitalWrite(motor_right[1], LOW);
delay(25);
}

void drive_forward(){
digitalWrite(motor_left[0], HIGH); 
digitalWrite(motor_left[1], LOW); 

digitalWrite(motor_right[0], HIGH); 
digitalWrite(motor_right[1], LOW); 
}

void drive_backward(){
digitalWrite(motor_left[0], LOW); 
digitalWrite(motor_left[1], HIGH); 

digitalWrite(motor_right[0], LOW); 
digitalWrite(motor_right[1], HIGH); 
}

void turn_left(){
digitalWrite(motor_left[0], LOW); 
digitalWrite(motor_left[1], HIGH); 

//digitalWrite(motor_right[0], HIGH); 
//digitalWrite(motor_right[1], LOW);
}

void turn_right(){
digitalWrite(motor_left[0], HIGH); 
digitalWrite(motor_left[1], LOW); 

digitalWrite(motor_right[0], LOW); 
digitalWrite(motor_right[1], HIGH); 
}
