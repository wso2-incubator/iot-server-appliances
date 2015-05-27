// this constant won't change.  It's the pin number
// of the sensor's output:
#define SONAR_TRIG  3
#define SONAR_ECHO  4


//obstacle
#define BUZZER 6
#define BUZZER_SOUND 100
#define MAX_DISTANCE 30

void setup() {
  // initialize serial communication:
  Serial.begin(9600);
}

void loop(){
  readSonar();
  delay(100);
}

void readSonar()
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
  if (cm > MAX_DISTANCE || cm <= 0){
    //Serial.println("Out of range");
    //noTone(BUZZER);
  }
  else {
    //pushSensorData("Sonar:"+ String(cm)); 
    //tone(BUZZER, BUZZER_SOUND);
   
  }
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

