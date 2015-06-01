## Folder Structure

* ActiveMQ sample config	-	a sample MQTT enabled **activemq.xml** file
* ArduinoAgents	-	the arduino sketeches for *Wifi/Ethernet* sheilds for the firealarm demo
* DC Config Files	-	Device-Cloud configuration folder (**/iot**) and the config files
* Dependant JARS	-	dependancies from ([carbon-device-mgt] (https://github.com/wso2/carbon-device-mgt.git "The WSO2 connected devices management framework on top our carbon") *&* [device-mgt] (https://github.com/wso2-dev/device-cloud/tree/master/components/device-mgt "A component of the WSO2 device cloud")) other repos that the firealarm JAX-RS relies on
* JAX-RS Service	-	The **.war** file of the *REST* service and the src code of the service which contains both the **FireAlarm** and **SenseBotRobot**


#### Building the **device-cloud** iteratively from:

* [carbon-device-mgt] (https://github.com/wso2/carbon-device-mgt.git "The WSO2 connected devices management framework on top of our carbon") 
* [device-cloud] (https://github.com/wso2-dev/device-cloud.git "components & features for device-cloud") 
* [product-device-cloud] (https://github.com/wso2-dev/product-device-cloud.git "The product device cloud repo")  

will enable the JAX-RS service to work fine without any further changes. The stats published could also be viewed via the device-cloud web UI.

The configurations for connecting to the **WSO2 BAM** and **ActiveMQ** need to be set accordingly in the device-cloud's configs *'xml'* file found at  ***repository/conf/iot/devicecloud-config.xml***

MQTT transport has to be enabled in the **"activemq.xml"** file found at ***<ACTIVE_MQ_HOME>/libexec/conf*** folder. It is set as: 
```xml
<transportConnectors>
  <transportConnector name="mqtt" uri="mqtt://0.0.0.0:1883?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600&amp;transport.defaultKeepAlive=60000"/>
</transportConnectors>
```

The IP can be left to localhost(0.0.0,0) if ActiveMQ is running on the same machine as the JAX-RS service. If not the IP of the machine has to set inside the <transportConnector> element. Accordingly this has to be updated in the devicecloud-config.xml configuration file. 
(I have attached my "activemq.xml" file which has MQTT transport enabled for localhost)


If by any chance wanting to deploy the JAX_RS in some other server (ex: WSO2_AS) it can be done by the following steps:


1. Rename the following files:

"iotdevices JAXRS service/iotdevices-dc" --> "iotdevices.war"

"Dependancy jars/org.wso2.carbon.device.mgt.common-0.9.1-jar" --> "org.wso2.carbon.device.mgt.common-0.9.1.jar"
"Dependancy jars/org.wso2.carbon.device.mgt.core-0.9.2-SNAPSHOT-jar" --> "org.wso2.carbon.device.mgt.core-0.9.2-SNAPSHOT.jar"
"Dependancy jars/org.wso2.carbon.device.mgt.iot.impl-1.9.2-SNAPSHOT-jar" --> "org.wso2.carbon.device.mgt.iot.impl-1.9.2-SNAPSHOT.jar"


1. Copy the "iotdevices.war" into repository/deployment/server/webapps/ folder

2. Make a folder named "iot" inside repository/conf/ and copy the "devicecloud-config.xml" and "iot-config.xml" (attached) inside that.

3. Copy the .jar dependancies that are attached into repository/components/lib 
             (these are dependancies on device-cloud and carbon-device-mgt repo's)

Make sure to edit the "devicecloud-config.xml" file inside the iot folder to set the appropiate configurations for the WSO2_BAM and MQTT (ActiveMQ) endpoints 





# (GitHub-Flavored) Markdown Editor

Basic useful feature list:

 * Ctrl+S / Cmd+S to save the file
 * Ctrl+Shift+S / Cmd+Shift+S to choose to save as Markdown or HTML
 * Drag and drop a file into here to load it
 * File contents are saved in the URL so you can share files


I'm no good at writing sample / filler text, so go write something yourself.

Look, a list!

 * foo
 * bar
 * baz

And here's some code! :+1:

```javascript
$(function(){
  $('div').html('I am a div.');
});
```

This is [on GitHub](https://github.com/jbt/markdown-editor) so let me know if I've b0rked it somewhere.


Props to Mr. Doob and his [code editor](http://mrdoob.com/projects/code-editor/), from which
the inspiration to this, and some handy implementation hints, came.

### Stuff used to make this:

 * [markdown-it](https://github.com/markdown-it/markdown-it) for Markdown parsing
 * [CodeMirror](http://codemirror.net/) for the awesome syntax-highlighted editor
 * [highlight.js](http://softwaremaniacs.org/soft/highlight/en/) for syntax highlighting in output code blocks
 * [js-deflate](https://github.com/dankogai/js-deflate) for gzipping of data to make it fit in URLs
