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

#define WLAN_SSID       "YourAccessPointSSID"           // cannot be longer than 32 characters!
#define WLAN_PASS       "APPassword"

#define WLAN_SECURITY   WLAN_SEC_WPA2
                           // Security can be WLAN_SEC_UNSEC, WLAN_SEC_WEP, WLAN_SEC_WPA or WLAN_SEC_WPA2
#define IDLE_TIMEOUT_MS  3000      

#define DEVICE_OWNER "${DEVICE_OWNER}"          
#define DEVICE_ID "${DEVICE_ID}"             



#define SERVICE_PORT 9763 
#define SERVICE_EPOINT "/iotdevices/SenseBotController/" 
                                        // pushalarmdata - application/json - {"owner":"","deviceId":"","replyMessage":"","time":"","key":"","value":""}
                                        // readcontrols/{owner}/{deviceId}
                                        // reply - application/json - {"owner":"","deviceId":"","replyMessage":""}

#define SONAR_TRIG  2
#define SONAR_ECHO  4
#define LDR_PIN  A1
#define TEMP_PIN A2
#define PIR_PIN  A3

#define BUZZER 6
#define BUZZER_SOUND 100
#define MAX_DISTANCE 30

#define TURN_DELAY 100

#define POLL_INTERVAL 1000
#define DEBUG false
#define CON_DEBUG true




#define LISTEN_PORT           80      // What TCP port to listen on for connections.  
                                      // The HTTP protocol uses port 80 by default.

#define MAX_ACTION            6      // Maximum length of the HTTP action that can be parsed.

#define MAX_PATH              10      // Maximum length of the HTTP request path that can be parsed.
                                      // There isn't much memory available so keep this short!

#define BUFFER_SIZE           MAX_ACTION + MAX_PATH + 10  // Size of buffer for incoming request data.
                                                          // Since only the first line is parsed this
                                                          // needs to be as large as the maximum action
                                                          // and path plus a little for whitespace and
                                                          // HTTP version.

#define TIMEOUT_MS            500    // Amount of time in milliseconds to wait for
                                     // an incoming request to finish.  Don't set this
                                     // too high or your server could be slow to respond.


#endif


