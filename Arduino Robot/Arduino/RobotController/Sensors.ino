int motionSense(){
  int motionDetect = digitalRead(PIR_PIN);
  if(DEBUG){
    Serial.print("MOTION : ");
    Serial.println(motionDetect);
  }
  pushSensorData("Motion:"+ String(motionDetect));
  return motionDetect;
}


int lightSense(){
  int lightLevel = digitalRead(LDR_PIN);
  if(DEBUG){
    Serial.print("LIGHT : ");
    Serial.println(lightLevel);
  }
  pushSensorData("LDR:"+String(lightLevel));
  return lightLevel;
}

double getTemperature(){
  dht DHT;
  int chk = DHT.read11(DHT11_PIN);
  if(DEBUG){
    Serial.println("-------------------------------");
    Serial.println("Type,\tstatus,\tHumidity (%),\tTemperature (C)");
    Serial.print("DHT11, \t");
  
  
  switch (chk)
  {
    case DHTLIB_OK:  
		Serial.print("OK,\t"); 
		break;
    case DHTLIB_ERROR_CHECKSUM: 
		Serial.print("Checksum error,\t"); 
		break;
    case DHTLIB_ERROR_TIMEOUT: 
		Serial.print("Time out error,\t"); 
		break;
    case DHTLIB_ERROR_CONNECT:
        Serial.print("Connect error,\t");
        break;
    case DHTLIB_ERROR_ACK_L:
        Serial.print("Ack Low error,\t");
        break;
    case DHTLIB_ERROR_ACK_H:
        Serial.print("Ack High error,\t");
        break;
    default: 
		Serial.print("Unknown error,\t"); 
		break;
  }
  // DISPLAY DATA
  Serial.print("\t");
  Serial.print(DHT.temperature, 1);
  Serial.print(",\t\t");
  Serial.println(DHT.humidity, 1);
  Serial.println("-------------------------------");
  }
  pushSensorData("Temperature:"+String(DHT.temperature));
  pushSensorData("Humidity:"+String(DHT.humidity));
  return temperature;
}


int getSonar()
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
    noTone(BUZZER);
  }
  else {
    tone(BUZZER, BUZZER_SOUND);
   
  }
if(DEBUG){
    Serial.print("SONAR : ");  
    Serial.print(cm);   
    Serial.print(" , ");   
    Serial.println(inches); 
    Serial.println("-----------------------------------");
}
    pushSensorData("Sonar:"+ String(sonar)); 
}

long microsecondsToInches(long microseconds){
    return microseconds / 74 / 2;
}

long microsecondsToCentimeters(long microseconds){
    return microseconds / 29 / 2;
}


static String sensedData="";
static int batchCount=0;

void pushSensorData(String value){
  float tm=(millis()-startTime)/1000;
  value=value+":"+String(tm);
  if(batchCount==0){
      sensedData=value;
  }else{
    
     sensedData=sensedData+","+value;
  }batchCount++;
  if(batchCount==BATCH_SIZE){
    Serial.println(sensedData);
    sensedData="";
    batchCount=0;
  }
  
}

