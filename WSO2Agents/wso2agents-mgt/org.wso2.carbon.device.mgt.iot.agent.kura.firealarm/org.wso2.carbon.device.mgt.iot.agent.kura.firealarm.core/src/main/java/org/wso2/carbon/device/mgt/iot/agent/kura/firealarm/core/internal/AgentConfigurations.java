package org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.internal;

public class AgentConfigurations {
	private String deviceOwner;
	private String deviceId;
	private String iotServerEndPoint;
	private String mqttBrokerEndPoint;
	private String xmppServerEndPoint;
	private String authenticationMethod;
	private String authenticationToken;
	private String refreshToken;
	private String networkInterface;
	private int dataPushInterval;

	public String getDeviceOwner() {
		return deviceOwner;
	}

	public void setDeviceOwner(String deviceOwner) {
		this.deviceOwner = deviceOwner;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getIotServerEndPoint() {
		return iotServerEndPoint;
	}

	public void setIotServerEndPoint(String iotServerEndPoint) {
		this.iotServerEndPoint = iotServerEndPoint;
	}

	public String getMqttBrokerEndPoint() {
		return mqttBrokerEndPoint;
	}

	public void setMqttBrokerEndPoint(String mqttBrokerEndPoint) {
		this.mqttBrokerEndPoint = mqttBrokerEndPoint;
	}

	public String getXmppServerEndPoint() {
		return xmppServerEndPoint;
	}

	public void setXmppServerEndPoint(String xmppServerEndPoint) {
		this.xmppServerEndPoint = xmppServerEndPoint;
	}

	public String getAuthenticationMethod() {
		return authenticationMethod;
	}

	public void setAuthenticationMethod(String authenticationMethod) {
		this.authenticationMethod = authenticationMethod;
	}

	public String getAuthenticationToken() {
		return authenticationToken;
	}

	public void setAuthenticationToken(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getNetworkInterface() {
		return networkInterface;
	}

	public void setNetworkInterface(String networkInterface) {
		this.networkInterface = networkInterface;
	}

	public int getDataPushInterval() {
		return dataPushInterval;
	}

	public void setDataPushInterval(int dataPushInterval) {
		this.dataPushInterval = dataPushInterval;
	}
}
