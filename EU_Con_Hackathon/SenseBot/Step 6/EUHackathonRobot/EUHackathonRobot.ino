#include "EUHackathonRobot.h"

#include <Adafruit_CC3000.h>
#include <SPI.h>
#include "dht.h"
#include <pt.h>

Adafruit_CC3000 cc3000 = Adafruit_CC3000(ADAFRUIT_CC3000_CS, ADAFRUIT_CC3000_IRQ, ADAFRUIT_CC3000_VBAT,
                                         SPI_CLOCK_DIVIDER); // you can change this clock speed

Adafruit_CC3000_Client pushClient;
    /**********************************************************************************************  
        0. Check with a sample Wifi code of the Adafruit_CC3000 library to ensure that the sheild is working
        1. Set the ip of the server(byte array below) where the Web-Rest API for the FireAlarm is running
        2. Check whether the "SERVICE_EPOINT" is correct in the 'EUHackothonRobot.h.h' file
        3. Check whether the "SERVICE_PORT" is the same (9763) for the server running. Change it if needed
        4. Check whether the pins have been attached accordingly in the Arduino
        5. Check whether all reqquired pins are added to the 'digitalPins' array  
    ***********************************************************************************************/

uint32_t sserver;
byte server[4] = { XX, XX, XX, XX };

String host, jsonPayLoad;
dht DHT;
  
void setup() {
  if(true) Serial.begin(115200); 
  pinMode(PIR_PIN, INPUT);
  pinMode(BUZZER, OUTPUT);
  
  connectHttp();
  setupResource();
}

void loop() {
  
  // USE A DELAY with millis when using this code with motor controlling or simply use the thread block
  if (pushClient.connected()) {   
    pushData();                    // batches all the required pin values together and pushes once
    delay(POLL_INTERVAL);
    
  } else {
    if(DEBUG) {
      Serial.println("client not found...");
      Serial.println("disconnecting.");
    }
    pushClient.close();
    cc3000.disconnect();  
   
    connectHttp();
  }  
}

