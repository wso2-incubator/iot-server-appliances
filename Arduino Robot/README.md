Arduino Robot
====================
An Arduino Robot controlled via Bluetooth communication. Multiple sensors are connected to the ArduinoUNO mounted onto the Robot. The data collected by the sensors are communicated to a python client (running on a PC) via bluetooth. This client in turn publishes this data to WSO2 CEP or BAM. 

Publishing the sensor data to BAM/CEP is enabled in two different ways:

	1 The Python client directly publishes the sensor data to the BAM/CEP Thrift-port
    2 The Python client publishes the sensor data to BAM/CEP by making a REST call to a service endpoint deployed in a WSO2-AS instance.

Prerequisites
--------------

* Robot Base + Gear Motors + Wheels + BatteryPack
* Arduino-to-Motor power amplifier IC
* ArduinoUNO 
* Arduino Sensor Sheilds (Sonar + LDR + PIR + GAS + DHT-Temperature)
* Arduino Bluetooth Sheild
* Python 2.7

Folder Structure
-----------------
* **Arduino** : Arduino code for Motor-Controller + Reading Sensor Input + Schematics to connect the circuits
* **PC_Clients** : Python clients to connect and motion-control the robot via bluetooth, to receive sensor data and to publish these data to WSO2 BAM 
* **RestService/ConnectedDevices** : Source code for the RESTService which is called by the python client to publish sensor-data to WSO2 BAM

Configurations
------------------

Change relevant information in the respective python client file.

There are two types of python clients available in the project. Please refer the ***README*** file inside the **Arduino Robot/PC_Clients/+** folder for more ndetails about it.

* ip = '192.168.1.2' &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; # IP address of the BAM/CEP server
* port = 7711 &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; # Thrift listen port of the BAM/CEP server
* username = 'admin' &nbsp; &nbsp; &nbsp; # username to connect to the BAM/CEP
* password = 'admin' &nbsp; &nbsp; &nbsp; # password to connect to the BAM/CEP

Run
------------

###### Arduino Setup

	1. Follow the schematics as shown in the "robot-motor_bb.png" & "robot-bb.png" to setup the circuitry. 
    		(Schematics Path: /Arduino Robot/Arduino/Schematics/)
            
	2. Download the "dht" library for the temperature sensor from
    		https://arduino-info.wikispaces.com/DHT11-Humidity-TempSensor
            
	3. Download the "protothread" library from
    		https://code.google.com/p/arduinode/downloads/detail?name=pt.zip
            
	4. Upload the arduino libraries from within the Arduino IDE: 
    		"Sketch -> Include Library -> Upload zip".


###### Python Client Setup
	1. Install PySerial Library 

		- Download the .tar.gz for "PySerial", according to your operating system and install the package for PySerial. 
		- Uncompress the download.
		- Move ("cd") into the folder and use - "sudo python setup.py install" in the terminal.
        
    2. Install the Requests Python Library
    	- Follow the instructions given the Installation page of the Requests library:
        	http://docs.python-requests.org/en/latest/user/install/#install

###### Setup BAM
Add the local machine ip to the file <BAM_HOME>/repository/conf/data-bridge/data-bridge-config.xml
set carbon port to 2
Follow the instruction on readme.xml

###### Setup AS
Deploay the war file on Application Server