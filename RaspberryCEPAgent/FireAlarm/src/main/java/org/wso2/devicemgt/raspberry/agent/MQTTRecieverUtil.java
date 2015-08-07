package org.wso2.devicemgt.raspberry.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by ace on 7/24/15.
 */
public class MQTTRecieverUtil implements  MqttCallback {

    private static final Log log = LogFactory.getLog(MQTTRecieverUtil.class);

    private MqttClient client;
    private String clientId;
    private MqttConnectOptions options;
    private String subscribeTopic;
    private String clientWillTopic;
    // topic needs to be set from outside
    private String controlQueueEndpoint;

    protected MQTTRecieverUtil(String owner, String deviceType, String controlQueueEndpoint,
                               String subscribeTopic) {
        this.clientId = owner + ":" + deviceType;
        this.subscribeTopic = subscribeTopic;
        this.clientWillTopic = deviceType + File.separator + "disconnection";
        this.controlQueueEndpoint = controlQueueEndpoint;
        this.initSubscriber(controlQueueEndpoint);
    }

    private void initSubscriber(String controlQueueEndpoint) {
        try {
            client = new MqttClient(controlQueueEndpoint, clientId, null);
            log.info("MQTT subscriber was created with ClientID : " + clientId);
        } catch (MqttException ex) {
            String errorMsg = "MQTT Client Error\n" + "\tReason:  " + ex.getReasonCode() +
                              "\n\tMessage: " + ex.getMessage() + "\n\tLocalMsg: " +
                              ex.getLocalizedMessage() + "\n\tCause: " + ex.getCause() +
                              "\n\tException: " + ex;
            log.error(errorMsg);
        }

        options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setWill(clientWillTopic, "connection crashed".getBytes(StandardCharsets.UTF_8), 2, true);
        client.setCallback(this);
    }

    /**
     * @return the whether subscriber is connected to queue
     */
    private boolean isConnected() {
        return client.isConnected();
    }

    public void subscribe() throws Exception {

        try {
            client.connect(options);
            log.info("Subscriber connected to queue at: " + controlQueueEndpoint);
        } catch (MqttSecurityException ex) {
            String errorMsg = "MQTT Security Exception when connecting to queue\n" + "\tReason:  " +
                              ex.getReasonCode() + "\n\tMessage: " + ex.getMessage() +
                              "\n\tLocalMsg: " + ex.getLocalizedMessage() + "\n\tCause: " +
                              ex.getCause() + "\n\tException: " + ex; //throw
            if (log.isDebugEnabled()) {
                log.debug(errorMsg);
            }
            throw new Exception(errorMsg, ex);

        } catch (MqttException ex) {
            String errorMsg = "MQTT Exception when connecting to queue\n" + "\tReason:  " +
                              ex.getReasonCode() + "\n\tMessage: " + ex.getMessage() +
                              "\n\tLocalMsg: " + ex.getLocalizedMessage() + "\n\tCause: " +
                              ex.getCause() + "\n\tException: " + ex; //throw
            if (log.isDebugEnabled()) {
                log.debug(errorMsg);
            }
            throw new Exception(errorMsg, ex);
        }

        try {
            client.subscribe(subscribeTopic, 0);

            log.info("Subscribed with client id: " + clientId);
            log.info("Subscribed to topic: " + subscribeTopic);
        } catch (MqttException ex) {
            String errorMsg = "MQTT Exception when trying to subscribe to topic: " +
                              subscribeTopic + "\n\tReason:  " + ex.getReasonCode() +
                              "\n\tMessage: " + ex.getMessage() + "\n\tLocalMsg: " +
                              ex.getLocalizedMessage() + "\n\tCause: " + ex.getCause() +
                              "\n\tException: " + ex;
            if (log.isDebugEnabled()) {
                log.debug(errorMsg);
            }
        }
    }

    @Override public void connectionLost(Throwable arg0) {
        log.warn("Lost Connection for client: " + this.clientId + " to " + controlQueueEndpoint);
        Thread subscriberDaemon = new Thread() {

            public void run() {
                while (true) {
                    if (!isConnected()) {
                        if (log.isDebugEnabled()) {
                            log.debug("Subscriber reconnecting to queue........");
                        }
                        try {
                            subscribe();
                        } catch (Exception e) {
                            if (log.isDebugEnabled()) {
                                log.debug("Could not reconnect and subscribe to ControlQueue.");
                            }
                        }
                    } else {
                        return;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        log.error("An Interrupted Exception in Subscriber thread.");
                    }
                }
            }
        };
        subscriberDaemon.setDaemon(true);
        subscriberDaemon.start();
    }

    @Override public void deliveryComplete(IMqttDeliveryToken arg0) {
        log.info("Message for client " + this.clientId + "delivered successfully.");
    }

    @Override public void messageArrived(final String topic, final MqttMessage message) {
        Thread subscriberThread = new Thread() {

            public void run() {
                postMessageArrived(topic, message);
            }
        };

        subscriberThread.start();
    }

    /*
    This method is used for post processing of a message.
     */
    protected void postMessageArrived(String topic, MqttMessage message){
        AgentConstants agentConstants = new AgentConstants();
        System.out.println("Topic : "+topic+ " Message : "+message);
        String fileLocation = agentConstants.prop.getProperty("execution.plan.file.location");
        writeToFile(new String(message.getPayload()),fileLocation);
    }


    private boolean writeToFile(String policy,String fileLocation){
        File file = new File(fileLocation);

        try (FileOutputStream fop = new FileOutputStream(file)) {

            // if file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = policy.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

            System.out.println("Done");
            AgentInitializer.setUpdated(true);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
