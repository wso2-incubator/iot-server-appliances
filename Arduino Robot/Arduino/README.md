### Folder Structure
-----------------
* **MotorTest** : Arduino Test-Code to ensure that the gear motors are working fine.
* **RobotController** : Complete Arduino Sketch with the necessary files to upload to the Arduino board.

		- MultipleSensors.h: Header file with "Sensor-Pin" details and constants.
        
        - Sensors.ino: Arduino code with methods that return readings from the sensors.
        
        - RobotController.ino: Main arduino code with the setup() and loop() methods which receives motor controls from the python client and sends back the sensor readings.
        
* **Schematics** : Schematic diagrams to connect the circuitry.

		- robot-motor_bb.png: Schematic diagram to connect Arduino with motor-current amplifier IC
        
        - robot_bb.png: Complete schematic diagram of the setup including sensors, arduino and the amplifier IC

