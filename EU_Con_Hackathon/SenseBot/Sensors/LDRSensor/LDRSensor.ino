#define LDR_PIN  A1
#define LOOP_DELAY  3000

void setup() {:
  Serial.begin(9600);
}

void loop() {
  lightSense();
  delay(LOOP_DELAY);
}


int lightSense(){
  int lightLevel = analogRead(LDR_PIN);
  
  Serial.print("LIGHT : ");
  Serial.println(lightLevel);
  
  return lightLevel;
}
