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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.communication.CommunicationHandler;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.operation.AgentOperationManager;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.utils.mqtt
		.MQTTCommunicationHandlerImpl;

public class AgentManager {

	private static final Log log = LogFactory.getLog(AgentManager.class);

	private static AgentManager thisInstance = new AgentManager();

	private AgentConfiguration agentConfigs;
	private AgentOperationManager agentOperationManager;
	private double temperature = 30, humidity = 30;

	private String xmppAdminJID;

	private String deviceIP;
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

		// Update Agent manager's temperature and humidity
		AgentCoreOperations.startGPIOReader();

		String mqttTopic = String.format(AgentConstants.MQTT_SUBSCRIBE_TOPIC,
		                                 agentConfigs.getDeviceOwner(),
		                                 agentConfigs.getDeviceId());

		CommunicationHandler mqttCommunicator = new MQTTCommunicationHandlerImpl(
				agentConfigs.getDeviceOwner(), agentConfigs.getDeviceId(),
				agentConfigs.getMqttBrokerEndpoint(), mqttTopic);

		mqttCommunicator.connect();
	}




	/*------------------------------------------------------------------------------------------*/
	/* 		            Getter and Setter Methods for the private variables                 	*/
	/*------------------------------------------------------------------------------------------*/

	public AgentConfiguration getAgentConfigs() {
		return agentConfigs;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getHumidity() {
		return humidity;
	}

	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}

	public AgentOperationManager getAgentOperationManager() {
		return agentOperationManager;
	}

	public void setAgentOperationManager(AgentOperationManager agentOperationManager) {
		this.agentOperationManager = agentOperationManager;
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
