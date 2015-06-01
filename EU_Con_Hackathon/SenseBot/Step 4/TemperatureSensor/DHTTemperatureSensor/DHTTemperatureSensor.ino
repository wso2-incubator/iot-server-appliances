#include "dht.h"

#define LOOP_DELAY  5000
#define TEMP_PIN 12

void setup() {
  Serial.begin(9600);
  Serial.println("Reading Temperature from DHT Sensor.............");
}

void loop() {
  getTemperature();
  delay(LOOP_DELAY);
}


double getTemperature(){
  dht DHT;
  int chk = DHT.read11(TEMP_PIN);
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
  return DHT.temperature;
}
