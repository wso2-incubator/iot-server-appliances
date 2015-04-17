## WSO2 BAM Integration
-----------------

This page provides the necessary utilities to setup WSO2 BAM to generate statistics from the sensor-data published to its datastore via the python client.

Get a broader understanding of configuring WSO2-BAM to publish data by reading the documentation in the following links:

		- The link provides the configurations for WSO2-APIM with WSO2-BAM to receive and view APIM data
		https://docs.wso2.com/display/AM180/Publishing+API+Runtime+Statistics

### Configurations
-----------------
* **MotorTest** : Arduino Test-Code to ensure that the gear motors are working fine.
* **RobotController** : Complete Arduino Sketch with the necessary files to upload to the Arduino board.

		- MultipleSensors.h: Header file with "Sensor-Pin" details and constants.
        
        - Sensors.ino: Arduino code with methods that return readings from the sensors.
        
        - RobotController.ino: Main arduino code with the setup() and loop() methods which receives motor controls from the python client and sends back the sensor readings.
        
* **Schematics** : Schematic diagrams to connect the circuitry.

		- robot-motor_bb.png: Schematic diagram to connect Arduino with motor-current amplifier IC
        
        - robot_bb.png: Complete schematic diagram of the setup including sensors, arduino and the amplifier IC


How to:
--------

1) Add the datasource WSO2_IOT_DB to the master-datasources.xml of BAM
2) Be sure to install the toolbox from the UI. Otherwise the dashboard won't appear.
4) Open the attached Client WSO2DevicePlatform-2.0.0.zip with IDE and run it, to propergate the sample data.
5) Go to the portal section under gadgets to view the dashboard
