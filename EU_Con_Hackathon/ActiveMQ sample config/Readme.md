MQTT transport has to be enabled in the **"activemq.xml"** file found at ***\<ACTIVE_MQ_HOME\>/libexec/conf*** folder. It is set as: 
```xml
<transportConnectors>
  <transportConnector name="mqtt" uri="mqtt://0.0.0.0:1883?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600&amp;transport.defaultKeepAlive=60000"/>
</transportConnectors>
```

The IP can be left to *localhost(0.0.0.0)* if ActiveMQ is running on the same machine as the JAX-RS service. If not the IP of the machine has to be set. Accordingly this has to be updated in the **devicecloud-config.xml** configuration file. 
