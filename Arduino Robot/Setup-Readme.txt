Arduino Setup
1. Follow the schematics
2. Download the dHt library from https://arduino-info.wikispaces.com/DHT11-Humidity-TempSensor
3. Sketch->include library ->upload zip.
4. Download the protothread library(https://code.google.com/p/arduinode/downloads/detail?name=pt.zip) and upload the zip


Python Client Setup
1.Install PySerial
	-Whatever your operating system, download the .tar.gz install package for PySerial 
	-uncompress
	-cd into the folder and use - sudo python setup.py install

Setup BAM
Add the local machine ip to the file <BAM_HOME>/repository/conf/data-bridge/data-bridge-config.xml
set carbon port to 2
Follow the instruction on readme.xml

Setup AS
Deploay the war file on Application Server