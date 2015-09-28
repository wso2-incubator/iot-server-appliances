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
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.constants.AgentConstants;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.exception
		.AgentCoreOperationException;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.operation.AgentOperationManager;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.operation.SimpleServer;

import java.util.Map;

public class AgentDataHolder {

	private static final Logger log = LoggerFactory.getLogger(AgentDataHolder.class);
	private static AgentDataHolder thisInstance = new AgentDataHolder();

	private AgentOperationManager agentOperationManager;
	private SimpleServer simpleServer;
	private String deviceIPAddress;
	private String iotServerEndPoint;
	private String deviceControllerAPIEndPoint;
	private String deviceIPRegistrationEP;
	private String pushDataAPIEndPoint;
	private AgentConfigurations agentConfigurations;

	private AgentDataHolder() {	}

	public static AgentDataHolder getInstance() {
		return thisInstance;
	}

	public void init() {
		this.agentConfigurations = AgentCoreOperations.readIoTServerConfigs();

		try {
			AgentCoreOperations.initializeHTTPEndPoints();
		} catch (AgentCoreOperationException e) {
			log.error(AgentConstants.LOG_APPENDER + "Error encountered whilst trying to initialize IoT-Server API EndPoints for the Device");
		}

		try {
			int responseCode = AgentCoreOperations.registerDeviceIP(this.agentConfigurations.getDeviceOwner(), this.agentConfigurations.getDeviceId());
			if (responseCode != HttpStatus.OK_200) {
				log.error(AgentConstants.LOG_APPENDER + "Device Registration with IoT Server at: " + this.iotServerEndPoint + " failed");
			}
		} catch (AgentCoreOperationException exception) {
			log.error(AgentConstants.LOG_APPENDER + "Error encountered whilst trying to register the Device's IP at: " + this.iotServerEndPoint);
		}

		AgentCoreOperations.pushDeviceData(this.agentConfigurations.getDeviceOwner(), this.agentConfigurations.getDeviceId(), this.agentConfigurations.getDataPushInterval());

		try {
			AgentCoreOperations.subscribeToMQTT(this.agentConfigurations.getDeviceOwner(), this.agentConfigurations.getDeviceId());
		} catch (AgentCoreOperationException e) {
			log.error(AgentConstants.LOG_APPENDER + "Subscription to MQTT Broker at: " + this.agentConfigurations.getMqttBrokerEndPoint() + " failed");
		}
		simpleServer = new SimpleServer();
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


	public String getDeviceIPAddress() {
		return deviceIPAddress;
	}

	public void setDeviceIPAddress(String deviceIPAddress) {
		this.deviceIPAddress = deviceIPAddress;
	}

	public String getIotServerEndPoint() {
		return iotServerEndPoint;
	}

	public void setIotServerEndPoint(String iotServerEndPoint) {
		this.iotServerEndPoint = iotServerEndPoint;
	}

	public String getDeviceControllerAPIEndPoint() {
		return deviceControllerAPIEndPoint;
	}

	public void setDeviceControllerAPIEndPoint(String deviceControllerAPIEndPoint) {
		this.deviceControllerAPIEndPoint = deviceControllerAPIEndPoint;
	}

	public String getDeviceIPRegistrationEP() {
		return deviceIPRegistrationEP;
	}

	public void setDeviceIPRegistrationEP(String deviceIPRegistrationEP) {
		this.deviceIPRegistrationEP = deviceIPRegistrationEP;
	}

	public String getPushDataAPIEndPoint() {
		return pushDataAPIEndPoint;
	}

	public void setPushDataAPIEndPoint(String pushDataAPIEndPoint) {
		this.pushDataAPIEndPoint = pushDataAPIEndPoint;
	}

	public AgentConfigurations getAgentConfigurations() {
		return agentConfigurations;
	}

	public void setAgentConfigurations(AgentConfigurations agentConfigurations) {
		this.agentConfigurations = agentConfigurations;
	}
}
