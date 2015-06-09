#ifndef EUHackathonRobot_H
#define EUHackathonRobot_H

#if (ARDUINO >= 100)
 #include "Arduino.h"
#else
 #include "WProgram.h"
#endif

// These are the interrupt and control pins
#define ADAFRUIT_CC3000_IRQ   3  // MUST be an interrupt pin!
// These can be any two pins
#define ADAFRUIT_CC3000_VBAT  5
#define ADAFRUIT_CC3000_CS    10

#define WLAN_SSID       "WSO2-Restricted"           // cannot be longer than 32 characters!
#define WLAN_PASS       "LKvene8xIOT"

#define WLAN_SECURITY   WLAN_SEC_WPA2
                           // Security can be WLAN_SEC_UNSEC, WLAN_SEC_WEP, WLAN_SEC_WPA or WLAN_SEC_WPA2
#define IDLE_TIMEOUT_MS  3000      

#define DEVICE_OWNER "Test" //"${DEVICE_OWNER}"          
#define DEVICE_ID "Test" //"${DEVICE_ID}"             



#define SERVICE_PORT 9763 
#define SERVICE_EPOINT "/iotdevices/SenseBotController/" 
                                        // pushalarmdata - application/json - {"owner":"","deviceId":"","replyMessage":"","time":"","key":"","value":""}
                                        // readcontrols/{owner}/{deviceId}
                                        // reply - application/json - {"owner":"","deviceId":"","replyMessage":""}


#define TEMP_PIN A2

#define TURN_DELAY 100

#define POLL_INTERVAL 1000
#define DEBUG false
#define CON_DEBUG true

#endif


