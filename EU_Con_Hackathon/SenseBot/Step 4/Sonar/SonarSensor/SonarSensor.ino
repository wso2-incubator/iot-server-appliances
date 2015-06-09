#define LOOP_DELAY  3000

#define SONAR_TRIG  A4
#define SONAR_ECHO  A5

int obstacleDistance;

void setup() {
  Serial.begin(9600);
  Serial.println("Sonar checking for obstacles.............");
}

void loop() {
  getSonar();
  delay(LOOP_DELAY); 
}




void getSonar()
{
  long duration, inches, cm;
  
  pinMode(SONAR_TRIG, OUTPUT);// attach pin 3 to Trig
  digitalWrite(SONAR_TRIG, LOW);
  delayMicroseconds(2);
  digitalWrite(SONAR_TRIG, HIGH);
  delayMicroseconds(5);
  digitalWrite(SONAR_TRIG, LOW);

  pinMode (SONAR_ECHO, INPUT);//attach pin 4 to Echo
  duration = pulseIn(SONAR_ECHO, HIGH);

  // convert the time into a distance
  inches = microsecondsToInches(duration);
  cm = microsecondsToCentimeters(duration);
  
  Serial.print("SONAR : ");  
  Serial.print(cm);   
  Serial.print(" , ");   
  Serial.println(inches); 
  Serial.println("-----------------------------------");
}

long microsecondsToInches(long microseconds){
    return microseconds / 74 / 2;
}

long microsecondsToCentimeters(long microseconds){
    return microseconds / 29 / 2;
}
