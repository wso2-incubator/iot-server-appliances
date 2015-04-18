class BAMPublisher:
	__deviceIP = "192.168.1.999"
	__deviceType = "ArduinoUNO"
	__deviceOwner = "SMEANTEST"
	__deviceMAC = "98:D3:31:80:38:D3"
	__description = "Indiana University Hackathon Sensor Data"

	metaData = [__deviceIP, __deviceType, __deviceOwner, 0]
	payloadData = [__deviceMAC, "", "", __description]
	# requestTime = None

	def setPublishStream(self, time, sensor, value):
		self.metaData[3] =int(round(time*1000))
		self.payloadData[1] = sensor
		self.payloadData[2] = value
		#print self.metaData
		# self.requestTime = long(round(time*1000))