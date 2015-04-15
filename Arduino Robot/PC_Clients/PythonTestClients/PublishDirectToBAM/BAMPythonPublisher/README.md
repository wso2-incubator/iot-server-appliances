Thrift Python Client
====================
This is a sample python client for WSO2 CEP (Complex Event Processor) and BAM (Business Activity Monitor) using thrift protocol

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
* ip = '192.168.1.2'	# IP address of the server
* port = 7711		# Thrift listen port of the server
* username = 'admin'	# username
* password = 'admin' 	# passowrd 

Run
------------
python PythonClient.py
