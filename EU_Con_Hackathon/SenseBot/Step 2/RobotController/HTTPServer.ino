#define WLAN_SSID       "WSO2-Restricted"           // cannot be longer than 32 characters!
#define WLAN_PASS       "LKvene8xIOT"

// Security can be WLAN_SEC_UNSEC, WLAN_SEC_WEP, WLAN_SEC_WPA or WLAN_SEC_WPA2
#define WLAN_SECURITY   WLAN_SEC_WPA2

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

Adafruit_CC3000_Server httpServer(LISTEN_PORT);
uint8_t buffer[BUFFER_SIZE+1];
int bufindex = 0;
char action[MAX_ACTION+1];
char path[MAX_PATH+1];

void initializeServer(void)
{
  Serial.println(F("WAIT!"));
 
 
  if (!cc3000.begin())
  {
    Serial.println(F("Couldn't begin()! Check your wiring?"));
    while(1);
  }
  byte mac[6] = { 0xc0, 0x4a, 0x00, 0x19, 0x6d, 0x1b };
   cc3000.setMacAddress(mac);
 
  if (!cc3000.connectToAP(WLAN_SSID, WLAN_PASS, WLAN_SECURITY)) {
    Serial.println(F("Failed!"));
    while(1);
  }
   
  Serial.println(F("Connected!"));
  
  Serial.println(F("Request DHCP"));
  while (!cc3000.checkDHCP())
  {
    delay(100); // ToDo: Insert a DHCP timeout!
  }  

  // Display the IP address DNS, Gateway, etc.
  while (! displayConnectionDetails()) {
    delay(1000);
  }  
  // Start listening for connections
  httpServer.begin();
  
  Serial.println(F("Listening for connections..."));
}

void listen()
{
  int direction = 0;
  
  // Try to get a client which is connected.
  Adafruit_CC3000_ClientRef client = httpServer.available();
  if (client) {
    //Serial.println(F("Client connected."));
    // Process this request until it completes or times out.
    // Note that this is explicitly limited to handling one request at a time!

    // Clear the incoming data buffer and point to the beginning of it.
    bufindex = 0;
    memset(&buffer, 0, sizeof(buffer));
    
    // Clear action and path strings.
    memset(&action, 0, sizeof(action));
    memset(&path,   0, sizeof(path));

    // Set a timeout for reading all the incoming data.
    unsigned long endtime = millis() + TIMEOUT_MS;
    
    // Read all the incoming data until it can be parsed or the timeout expires.
    bool parsed = false;
    while (!parsed && (millis() < endtime) && (bufindex < BUFFER_SIZE)) {
      if (client.available()) {
        buffer[bufindex++] = client.read();
      }
      parsed = parseRequest(buffer, bufindex, action, path);
    }

    // Handle the request if it was parsed.
    if (parsed) {
//      Serial.println(F("Processing request"));
//      Serial.print(F("Action: ")); Serial.println(action);
      Serial.print(F("Path: ")); Serial.println(path);
      // Check the action to see if it was a GET request.
      if (strcmp(action, "GET") == 0) {
              String urlPath = path;
              urlPath.replace("/move/","");
              urlPath.replace("/","");
//              client.fastrprint(F("Command Received")); 
              if(urlPath.endsWith("L")){
                
                updateDirectionVariable(3);
 
              }else if(urlPath.endsWith("R")){
               
                updateDirectionVariable(4);
 
              }else if(urlPath.endsWith("B")){
               
                updateDirectionVariable(2);
  
              }else if(urlPath.endsWith("F")){
                //Serial.println("test");
                updateDirectionVariable(1);

              }else if(urlPath.endsWith("S")){
               
                updateDirectionVariable(5);

              }
      }
//      else {
//        // Unsupported action, respond with an HTTP 405 method not allowed error.
//        //client.fastrprintln(F("HTTP/1.1 405 Method Not Allowed"));
//        //client.fastrprintln(F(""));
//      }
    }

    // Wait a short period to make sure the response had time to send before
    // the connection is closed (the CC3000 sends data asyncronously).
    delay(100);

    // Close the connection when done.
    Serial.println(F("Client disconnected"));
    client.close();
    //return direction;
  }
}


bool parseRequest(uint8_t* buf, int bufSize, char* action, char* path) {
  // Check if the request ends with \r\n to signal end of first line.
  if (bufSize < 2)
    return false;
  if (buf[bufSize-2] == '\r' && buf[bufSize-1] == '\n') {
    parseFirstLine((char*)buf, action, path);
    return true;
  }
  return false;
}

// Parse the action and path from the first line of an HTTP request.
void parseFirstLine(char* line, char* action, char* path) {
  // Parse first word up to whitespace as action.
  char* lineaction = strtok(line, " ");
  if (lineaction != NULL)
    strncpy(action, lineaction, MAX_ACTION);
  // Parse second word up to whitespace as path.
  char* linepath = strtok(NULL, " ");
  if (linepath != NULL)
    strncpy(path, linepath, MAX_PATH);
}

// Tries to read the IP address and other connection details
bool displayConnectionDetails(void)
{
  uint32_t ipAddress, netmask, gateway, dhcpserv, dnsserv;
  
  if(!cc3000.getIPAddress(&ipAddress, &netmask, &gateway, &dhcpserv, &dnsserv))
  {
    Serial.println(F("Unable to retrieve the IP Address!\r\n"));
    return false;
  }
  else
  {
    Serial.print(F("\nIP Addr: ")); cc3000.printIPdotsRev(ipAddress);
//    Serial.print(F("\nNetmask: ")); cc3000.printIPdotsRev(netmask);
//    Serial.print(F("\nGateway: ")); cc3000.printIPdotsRev(gateway);
//    Serial.print(F("\nDHCPsrv: ")); cc3000.printIPdotsRev(dhcpserv);
//    Serial.print(F("\nDNSserv: ")); cc3000.printIPdotsRev(dnsserv);
    Serial.println();
    return true;
  }
}
