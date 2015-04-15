Thrift Python Client
====================
Sample python library for WSO2 CEP (Complex Event Processor) and BAM (Business Activity Monitor) using thrift protocol. Configure the "PythonClient.py" file as explained below for a quick test of this library. 

The sample was borrwed from: https://github.com/dineshbandara/thrift-python-client.

Modifications were done to the original code in order to accept and publish data with complex stream definitions including metadata.

Prerequisites
--------------

* Python 2.x

Folder Structure
-----------------
* gen-py : Source code generated via Thrift compiler for relevent Thrift files
* thrift : Set of Python packages provided by Thrift. Can be found at [THRIFT_SOURCE]/lib/py/src

Configurations
------------------

Change relevant informations in PythonClient.py
* ip = '192.168.1.2'	# IP address of the BAM/CEP server
* port = 7711		# Thrift listen port of the BAM/CEP server
* username = 'admin'	# username
* password = 'admin' 	# password 

Run
------------
python PythonClient.py


Arduino Setup
1. Follow the schematics
2. Download the dHt library from https://arduino-info.wikispaces.com/DHT11-Humidity-TempSensor
3. Sketch -> Include Library -> Upload zip.
4. Download the protothread library(https://code.google.com/p/arduinode/downloads/detail?name=pt.zip) and upload the zip as explined in step 3.


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