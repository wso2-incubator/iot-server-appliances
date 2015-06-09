#define LOOP_DELAY  4000

#define PIR_PIN  A3
#define LED_PIN  4

int motionDetect;

void setup() {
  pinMode(PIR_PIN, INPUT);
  pinMode(LED_PIN, OUTPUT);
  Serial.begin(9600);
  Serial.println("Beginning Motion Sense.............");
}

void loop() {
  motionDetect = digitalRead(PIR_PIN);    // could use the method call below as well
  if (motionDetect == HIGH) {
    Serial.print("Detected- ");
    Serial.println(motionDetect);
    digitalWrite(LED_PIN, HIGH);
    delay(LOOP_DELAY);
  } else {
    Serial.print("No Object - ");
    Serial.println(motionDetect);
    digitalWrite(LED_PIN, LOW);
    delay(LOOP_DELAY);
  }
}

