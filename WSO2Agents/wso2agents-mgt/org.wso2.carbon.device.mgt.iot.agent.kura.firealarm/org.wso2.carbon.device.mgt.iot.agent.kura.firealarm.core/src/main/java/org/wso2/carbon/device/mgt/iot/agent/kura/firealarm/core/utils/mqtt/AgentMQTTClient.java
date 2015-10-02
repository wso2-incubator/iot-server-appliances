/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.utils.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.constants.AgentConstants;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.exception
		.AgentCoreOperationException;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AgentMQTTClient implements MqttCallback {
	private static final Logger log = LoggerFactory.getLogger(AgentMQTTClient.class);

	private MqttClient client;
	private String clientId;
	private MqttConnectOptions options;
	private String subscribeTopic;
	private String clientWillTopic;
	private String mqttBrokerEndPoint;
	private int reConnectionInterval;

	protected AgentMQTTClient(String deviceOwner, String deviceType, String mqttBrokerEndPoint,
	                          String subscribeTopic) {
		this.clientId = deviceOwner + ":" + deviceType;
		this.subscribeTopic = subscribeTopic;
		this.clientWillTopic = deviceType + File.separator + "disconnection";
		this.mqttBrokerEndPoint = mqttBrokerEndPoint;
		this.reConnectionInterval = AgentConstants.DEFAULT_MQTT_RECONNECTION_INTERVAL;
		this.initSubscriber();
	}

	protected AgentMQTTClient(String deviceOwner, String deviceType, String mqttBrokerEndPoint,
	                          String subscribeTopic, int reConnectionInterval) {
		this.clientId = deviceOwner + ":" + deviceType;
		this.subscribeTopic = subscribeTopic;
		this.clientWillTopic = deviceType + File.separator + "disconnection";
		this.mqttBrokerEndPoint = mqttBrokerEndPoint;
		this.reConnectionInterval = reConnectionInterval;
		this.initSubscriber();
	}

	private void initSubscriber() {
		try {
			client = new MqttClient(this.mqttBrokerEndPoint, clientId, null);
			log.info(AgentConstants.LOG_APPENDER + "MQTT subscriber was created with ClientID : " +
					         clientId);
		} catch (MqttException ex) {
			String errorMsg = "MQTT Client Error\n" + "\tReason:  " + ex.getReasonCode() +
					"\n\tMessage: " + ex.getMessage() + "\n\tLocalMsg: " +
					ex.getLocalizedMessage() + "\n\tCause: " + ex.getCause() +
					"\n\tException: " + ex;
			log.error(AgentConstants.LOG_APPENDER + errorMsg);
		}

		options = new MqttConnectOptions();
		options.setCleanSession(false);
		options.setWill(clientWillTopic, "connection crashed".getBytes(StandardCharsets.UTF_8), 2,
		                true);
		client.setCallback(this);
	}


	private boolean isConnected() {
		return client.isConnected();
	}

	public void subscribe() throws AgentCoreOperationException {
		try {
			client.connect(options);
			log.info(AgentConstants.LOG_APPENDER + "Subscriber connected to queue at: " +
					         this.mqttBrokerEndPoint);
		} catch (MqttSecurityException ex) {
			String errorMsg = "MQTT Security Exception when connecting to queue\n" + "\tReason: " +
					" " +
					ex.getReasonCode() + "\n\tMessage: " + ex.getMessage() +
					"\n\tLocalMsg: " + ex.getLocalizedMessage() + "\n\tCause: " +
					ex.getCause() + "\n\tException: " + ex; //throw
			if (log.isDebugEnabled()) {
				log.debug(AgentConstants.LOG_APPENDER + errorMsg);
			}
			throw new AgentCoreOperationException(errorMsg, ex);

		} catch (MqttException ex) {
			String errorMsg = "MQTT Exception when connecting to queue\n" + "\tReason:  " +
					ex.getReasonCode() + "\n\tMessage: " + ex.getMessage() +
					"\n\tLocalMsg: " + ex.getLocalizedMessage() + "\n\tCause: " +
					ex.getCause() + "\n\tException: " + ex; //throw
			if (log.isDebugEnabled()) {
				log.debug(AgentConstants.LOG_APPENDER + errorMsg);
			}
			throw new AgentCoreOperationException(errorMsg, ex);
		}

		try {
			client.subscribe(subscribeTopic, 0);

			log.info(AgentConstants.LOG_APPENDER + "Subscriber - " + clientId +
					         " subscribed to topic: " + subscribeTopic);
		} catch (MqttException ex) {
			String errorMsg = "MQTT Exception when trying to subscribe to topic: " +
					subscribeTopic + "\n\tReason:  " + ex.getReasonCode() +
					"\n\tMessage: " + ex.getMessage() + "\n\tLocalMsg: " +
					ex.getLocalizedMessage() + "\n\tCause: " + ex.getCause() +
					"\n\tException: " + ex;
			if (log.isDebugEnabled()) {
				log.debug(AgentConstants.LOG_APPENDER + errorMsg);
			}
			throw new AgentCoreOperationException(errorMsg, ex);
		}
	}

	public void connectionLost(Throwable throwable) {
		log.warn(AgentConstants.LOG_APPENDER + "Lost Connection for client: " + this.clientId +
				         " to " + this.mqttBrokerEndPoint);

		Runnable reSubscriber = new Runnable() {
			@Override
			public void run() {
				if (!isConnected()) {
					if (log.isDebugEnabled()) {
						log.debug(AgentConstants.LOG_APPENDER +
								          "Subscriber reconnecting to queue........");
					}
					try {
						subscribe();
					} catch (AgentCoreOperationException e) {
						if (log.isDebugEnabled()) {
							log.debug(AgentConstants.LOG_APPENDER +
									          "Could not reconnect and subscribe to ControlQueue" +
									          ".");
						}
					}
				} else {
					return;
				}
			}
		};

		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(reSubscriber, 0, this.reConnectionInterval, TimeUnit.SECONDS);
	}

	public void messageArrived(final String topic, final MqttMessage mqttMessage) throws
	                                                                              Exception {
		Thread subscriberThread = new Thread() {
			public void run() {
				postMessageArrived(topic, mqttMessage);
			}
		};
		subscriberThread.start();
	}

	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
		String message = "";
		try {
			message = iMqttDeliveryToken.getMessage().toString();
		} catch (MqttException e) {
			log.error(AgentConstants.LOG_APPENDER +
					          "Error occurred whilst trying to read the message from the MQTT " +
					          "delivery token" +
					          ".");
		}
		String topic = iMqttDeliveryToken.getTopics()[0];
		String client = iMqttDeliveryToken.getClient().getClientId();
		log.info(AgentConstants.LOG_APPENDER + "Message - '" + message + "' of client [" + client +
				         "] for the topic (" + topic + ") was delivered successfully.");
	}

	/**
	 * This method is used for post processing of a received message. This method will be
	 * implemented as per the need of the subscriber object.
	 *
	 * @param topic   The Topic for which the message was received for.
	 * @param message The message received for the subscription to the above topic.
	 */
	protected abstract void postMessageArrived(String topic, MqttMessage message);

	public MqttClient getClient() {
		return client;
	}

}
