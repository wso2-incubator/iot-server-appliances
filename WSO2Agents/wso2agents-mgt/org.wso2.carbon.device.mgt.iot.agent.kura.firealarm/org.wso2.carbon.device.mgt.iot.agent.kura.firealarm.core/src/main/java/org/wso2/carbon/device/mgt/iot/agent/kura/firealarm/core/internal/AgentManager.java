/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.internal;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.exception
		.AgentCoreOperationException;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.operation.AgentOperationManager;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.utils.http.SimpleServer;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.utils.mqtt.MQTTClient;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.utils.xmpp.XMPPClient;

public class AgentManager {

	private static final Logger log = LoggerFactory.getLogger(AgentManager.class);
	private static AgentManager thisInstance = new AgentManager();

	private AgentConfiguration agentConfigs;
	private AgentOperationManager agentOperationManager;

	private SimpleServer simpleServer;
	private MQTTClient agentMQTTClient;
	private XMPPClient agentXMPPClient;
	private String xmppAdminJID;

	private String deviceIP;
	private String iotServerEP;
	private String controllerAPIEP;
	private String ipRegistrationEP;
	private String pushDataAPIEP;


	private AgentManager() {
	}

	public static AgentManager getInstance() {
		return thisInstance;
	}

	public void init() {
		// Read IoT-Server specific configurations from the 'deviceConfig.properties' file
		this.agentConfigs = AgentCoreOperations.readIoTServerConfigs();

		// Initialise IoT-Server URL endpoints from the configuration read from file
		AgentCoreOperations.initializeHTTPEndPoints();

		// Register this current device's IP with the IoT-Server
		try {
			int responseCode = AgentCoreOperations.registerDeviceIP(
					this.agentConfigs.getDeviceOwner(),
					this.agentConfigs.getDeviceId());

			if (responseCode != HttpStatus.OK_200) {
				log.error(AgentConstants.LOG_APPENDER + "Device Registration with IoT Server at:" +
						          " " + this.iotServerEP + " failed");
			}
		} catch (AgentCoreOperationException exception) {
			log.error(AgentConstants.LOG_APPENDER +
					          "Error encountered whilst trying to register the Device's IP at: " +
					          this.iotServerEP);
		}

		// Initiate the thread for continuous pushing of device data to the IoT-Server
		AgentCoreOperations.initiateDeviceDataPush(this.agentConfigs.getDeviceOwner(),
		                                           this.agentConfigs.getDeviceId(),
		                                           this.agentConfigs.getDataPushInterval());

		// Subscribe to the platform's MQTT Queue for receiving Control Signals via MQTT
		try {
			AgentCoreOperations.subscribeToMQTT(this.agentConfigs.getDeviceOwner(),
			                                    this.agentConfigs.getDeviceId(),
			                                    this.agentConfigs.getMqttBrokerEP());
		} catch (AgentCoreOperationException e) {
			log.error(AgentConstants.LOG_APPENDER + "Subscription to MQTT Broker at: " +
					          this.agentConfigs.getMqttBrokerEP() + " failed");
			retryMQTTSubscription();
		}

		// Connect to the platform's XMPP Server for receiving Control Signals via XMPP
		try {
			AgentCoreOperations.connectToXMPPServer(this.agentConfigs.getDeviceId(),
			                                        this.agentConfigs.getAuthToken(),
			                                        this.agentConfigs.getDeviceOwner(),
			                                        this.agentConfigs.getXmppServerEP());
		} catch (AgentCoreOperationException e) {
			log.error(AgentConstants.LOG_APPENDER + "Connect/Login attempt to XMPP Server at: " +
					          this.agentConfigs.getXmppServerEP() + " failed");
			retryXMPPConnection();
		}

		// Start a simple HTTP Server to receive Control Signals via HTTP
		try {
			simpleServer = new SimpleServer();
		} catch (AgentCoreOperationException e) {
			log.error(AgentConstants.LOG_APPENDER + "Failed to start HTTP Server");
			retryHTTPServerInit();
		}
	}


	private void retryMQTTSubscription() {
		Thread retryToSubscribe = new Thread() {
			@Override
			public void run() {
				while (true) {
					if (!agentMQTTClient.isConnected()) {
						if (log.isDebugEnabled()) {
							log.debug(AgentConstants.LOG_APPENDER +
									          "Subscriber re-trying to reach MQTT queue....");
						}

						try {
							agentMQTTClient.connectAndSubscribe();
						} catch (AgentCoreOperationException e1) {
							if (log.isDebugEnabled()) {
								log.debug(AgentConstants.LOG_APPENDER +
										          "Attempt to re-connect to MQTT-Queue " +
										          "failed");
							}
						}
					} else {
						break;
					}

					try {
						Thread.sleep(AgentConstants.DEFAULT_RETRY_THREAD_INTERVAL);
					} catch (InterruptedException e1) {
						log.error("MQTT: Thread S;eep Interrupt Exception");
					}
				}
			}
		};

		retryToSubscribe.setDaemon(true);
		retryToSubscribe.start();
	}

	private void retryXMPPConnection() {
		Thread retryToConnect = new Thread() {
			@Override
			public void run() {

				while (true) {
					if (!agentXMPPClient.isConnected()) {
						if (log.isDebugEnabled()) {
							log.debug(AgentConstants.LOG_APPENDER +
									          "Re-trying to reach XMPP Server....");
						}

						try {
							agentXMPPClient.connectAndLogin(agentConfigs.getDeviceId(),
							                                agentConfigs.getAuthToken(),
							                                agentConfigs.getDeviceOwner());
							agentXMPPClient.setMessageFilterAndListener(xmppAdminJID);
						} catch (AgentCoreOperationException e1) {
							if (log.isDebugEnabled()) {
								log.debug(AgentConstants.LOG_APPENDER +
										          "Attempt to re-connect to XMPP-Server " +
										          "failed");
							}
						}
					} else {
						break;
					}

					try {
						Thread.sleep(AgentConstants.DEFAULT_RETRY_THREAD_INTERVAL);
					} catch (InterruptedException e1) {
						log.error("XMPP: Thread Sleep Interrupt Exception");
					}
				}
			}
		};

		retryToConnect.setDaemon(true);
		retryToConnect.start();
	}

	private void retryHTTPServerInit() {
		Thread restartServer = new Thread() {
			@Override
			public void run() {
				while (true) {
					if (!simpleServer.getServer().isStarted()) {
						if (log.isDebugEnabled()) {
							log.debug(AgentConstants.LOG_APPENDER +
									          "Re-trying to start HTTP Server....");
						}

						try {
							simpleServer.getServer().start();
						} catch (Exception e) {
							if (log.isDebugEnabled()) {
								log.debug(AgentConstants.LOG_APPENDER +
										          "Attempt to restart HTTP-Server failed");
							}
						}
					} else {
						break;
					}

					try {
						Thread.sleep(AgentConstants.DEFAULT_RETRY_THREAD_INTERVAL);
					} catch (InterruptedException e1) {
						log.error("HTTP: Thread Sleep Interrupt Exception");
					}
				}
			}
		};

		restartServer.setDaemon(true);
		restartServer.start();
	}

	/*------------------------------------------------------------------------------------------*/
	/* 		            Getter and Setter Methods for the private variables                 	*/
	/*------------------------------------------------------------------------------------------*/

	public AgentConfiguration getAgentConfigs() {
		return agentConfigs;
	}

	public void setAgentConfigs(AgentConfiguration agentConfigs) {
		this.agentConfigs = agentConfigs;
	}

	public AgentOperationManager getAgentOperationManager() {
		return agentOperationManager;
	}

	public void setAgentOperationManager(AgentOperationManager agentOperationManager) {
		this.agentOperationManager = agentOperationManager;
	}

	public SimpleServer getSimpleServer() {
		return simpleServer;
	}

	public MQTTClient getAgentMQTTClient() {
		return agentMQTTClient;
	}

	public void setAgentMQTTClient(
			MQTTClient mqttClient) {
		this.agentMQTTClient = mqttClient;
	}

	public XMPPClient getAgentXMPPClient() {
		return agentXMPPClient;
	}

	public void setAgentXMPPClient(
			XMPPClient xmppClient) {
		this.agentXMPPClient = xmppClient;
	}

	public String getXmppAdminJID() {
		return xmppAdminJID;
	}

	public void setXmppAdminJID(String xmppAdminJID) {
		this.xmppAdminJID = xmppAdminJID;
	}

	public String getDeviceIP() {
		return deviceIP;
	}

	public void setDeviceIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}

	public String getIotServerEP() {
		return iotServerEP;
	}

	public void setIotServerEP(String iotServerEP) {
		this.iotServerEP = iotServerEP;
	}

	public String getControllerAPIEP() {
		return controllerAPIEP;
	}

	public void setControllerAPIEP(String controllerAPIEP) {
		this.controllerAPIEP = controllerAPIEP;
	}

	public String getIpRegistrationEP() {
		return ipRegistrationEP;
	}

	public void setIpRegistrationEP(String ipRegistrationEP) {
		this.ipRegistrationEP = ipRegistrationEP;
	}

	public String getPushDataAPIEP() {
		return pushDataAPIEP;
	}

	public void setPushDataAPIEP(String pushDataAPIEP) {
		this.pushDataAPIEP = pushDataAPIEP;
	}
}
