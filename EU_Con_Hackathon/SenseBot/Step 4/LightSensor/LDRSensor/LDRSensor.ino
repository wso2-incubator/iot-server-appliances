#define LDR_PIN  A1
#define LDR_D_PIN   0
#define LOOP_DELAY  3000

void setup() {
  Serial.begin(9600);
  pinMode(LDR_D_PIN, OUTPUT);
}

void loop() {
  lightSense();
  delay(LOOP_DELAY);
}


int lightSense(){
  int lightLevel = analogRead(LDR_PIN);
//  int lightLevel = digitalRead(LDR_D_PIN);
  
  Serial.print("LIGHT : ");
  Serial.println(lightLevel);
  
  return lightLevel;
}
