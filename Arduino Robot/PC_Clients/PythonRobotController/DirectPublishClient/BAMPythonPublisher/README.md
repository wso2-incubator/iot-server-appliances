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
