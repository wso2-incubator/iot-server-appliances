package org.wso2.devicemgt.raspberry.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by ace on 7/25/15.
 */
public class MQTTPolicyReciever implements Runnable {
    private static Log log = LogFactory.getLog(MQTTPolicyReciever.class);
    final AgentConstants constants = new AgentConstants();

    public void run(){
        MQTTRecieverUtil mqttReciever = new MQTTRecieverUtil("Subscriber", "Raspberry-Pi", constants.prop.getProperty("mqtt.url"),
                                                             constants.prop.getProperty("mqtt.topic.name"));
        try {
            mqttReciever.subscribe();
        } catch (Exception e) {
            log.error("Error when invoking MQTT");
        }
    }
}
