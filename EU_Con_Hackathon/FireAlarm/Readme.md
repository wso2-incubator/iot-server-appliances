## Folder Structure

* ActiveMQ sample config	-	a sample MQTT enabled **activemq.xml** file
* ArduinoAgents	-	the arduino sketeches for *Wifi/Ethernet* sheilds for the firealarm demo
* DC Config Files	-	Device-Cloud configuration folder (**/iot**) and the config files
* Dependant JARS	-	dependancies from ([carbon-device-mgt] (https://github.com/wso2/carbon-device-mgt.git "The WSO2 connected devices management framework on top our carbon") *&* [device-mgt] (https://github.com/wso2-dev/device-cloud/tree/master/components/device-mgt "A component of the WSO2 device cloud")) other repos that the firealarm JAX-RS relies on
* JAX-RS Service	-	The **.war** file of the *REST* service and the src code of the service which contains both the **FireAlarm** and **SenseBotRobot**

***********
#### Building the **device-cloud** iteratively from:

* [carbon-device-mgt] (https://github.com/wso2/carbon-device-mgt.git "The WSO2 connected devices management framework on top of our carbon") 
* [device-cloud] (https://github.com/wso2-dev/device-cloud.git "components & features for device-cloud") 
* [product-device-cloud] (https://github.com/wso2-dev/product-device-cloud.git "The product device cloud repo")  

will enable the JAX-RS service to work fine without any further changes. The stats published could also be viewed via the device-cloud web UI.

The configurations for connecting to the **WSO2 BAM** and **ActiveMQ** need to be set accordingly in the device-cloud's configs *'xml'* file found at  ***repository/conf/iot/devicecloud-config.xml***

MQTT transport has to be enabled in the **"activemq.xml"** file found at ***\<ACTIVE_MQ_HOME\>/libexec/conf*** folder. It is set as: 
```xml
<transportConnectors>
  <transportConnector name="mqtt" uri="mqtt://0.0.0.0:1883?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600&amp;transport.defaultKeepAlive=60000"/>
</transportConnectors>
```

The IP can be left to *localhost(0.0.0.0)* if ActiveMQ is running on the same machine as the JAX-RS service. If not the IP of the machine has to be set. Accordingly this has to be updated in the **devicecloud-config.xml** configuration file. 

***********

##### If wanting to deploy the JAX_RS in some other server (ex: WSO2_AS) it can be done by the following steps:

1. Rename the **"iotdevices-war"** *(found inside the 'JAX-RS Service' folder)* into **"iotdevices.war"**

2. Copy that **"iotdevices.war"** into **repository/deployment/server/webapps/** folder

3. Make a folder named **"iot"** inside **repository/conf/** and copy into it the *"devicecloud-config.xml"* and *"iot-config.xml"* *(found inside the 'DC Config Files/iot' folder)*.

4. Copy the *.jar* dependancies *(found inside the 'Dependant JARS' folder)* into **repository/components/lib** 

5. Finally edit the **"devicecloud-config.xml"** file inside the iot folder *(that was copied earlier)* to set the appropiate configurations for the **WSO2 BAM** and **MQTT (ActiveMQ)** endpoints 





