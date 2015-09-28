package org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.constants;

import java.io.File;

public class AgentConstants {
	public static final String DEVICE_TYPE = "firealarm";

	private static final String FILE_SEPERATOR = File.separator;
	public static final String LOG_APPENDER = "AGENT_LOG:: ";
	public static final String PROPERTIES_FILE_PATH = "." + FILE_SEPERATOR + "repository" + FILE_SEPERATOR + "conf" + FILE_SEPERATOR;

	public static final String HTTP_PREFIX = "http://";
	public static final String HTTP_POST = "POST";
	public static final String HTTP_GET = "GET";
	public static final String APPLICATION_JSON_TYPE = "application/json";

	public static final int DEVICE_SERVER_PORT = 9090;

	public static final String DEVICE_CONTROLLER_API_EP = "/firealarm/controller";
	public static final String DEVICE_REGISTER_API_EP = "/register";
	public static final String DEVICE_PUSH_TEMPERATURE_API_EP = "/push_temperature";
	public static final String PUSH_DATA_PAYLOAD = "{\"owner\":\"%s\",\"deviceId\":\"%s\",\"reply\":\"%s\",\"value\":\"%s\"}";

	public static final int DEFAULT_MQTT_RECONNECTION_INTERVAL = 2;
	public static final int DEFAULT_MQTT_QUALITY_OF_SERVICE = 0;
	public static final String MQTT_SUBSCRIBE_TOPIC = "wso2/iot/%s/" + DEVICE_TYPE + "/%s";
	public static final String MQTT_TEMP_PUBLISH_TOPIC = "wso2/iot/%s/" + DEVICE_TYPE + "/%s/temp";
	public static final String MQTT_HUMID_PUBLISH_TOPIC = "wso2/iot/%s/" + DEVICE_TYPE + "/%s/humid";

	public static final String AGENT_PROPERTIES_FILE_NAME = "deviceConfig.properties";

	public static final String DEVICE_OWNER_PROPERTY = "owner";
	public static final String DEVICE_ID_PROPERTY = "deviceId";
	public static final String IOT_SERVER_EP_PROPERTY = "server-ep";
	public static final String MQTT_BROKER_EP_PROPERTY = "mqtt-ep";
	public static final String XMPP_SERVER_EP_PROPERTY = "xmpp-ep";
	public static final String AUTH_METHOD_PROPERTY = "auth-method";
	public static final String AUTH_TOKEN_PROPERTY = "auth-token";
	public static final String REFRESH_TOKEN_PROPERTY = "refresh-token";
	public static final String NETWORK_INTERFACE_PROPERTY = "network-interface";
	public static final String PUSH_INTERVAL_PROPERTY = "push-interval";

	public static final String DEFAULT_DEVICE_OWNER = "admin";
	public static final String DEFAULT_DEVICE_ID = "1234567890";
	public static final String DEFAULT_IOT_SERVER_EP = "127.0.0.1:9763";
	public static final String DEFAULT_MQTT_BROKER_EP = "127.0.0.1:1883";
	public static final String DEFAULT_XMPP_SERVER_EP = "127.0.0.1:9061";
	public static final String DEFAULT_AUTH_METHOD = "token";
	public static final String DEFAULT_AUTH_TOKEN = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0987654321";
	public static final String DEFAULT_REFRESH_TOKEN = "1234567890ZYXWVUTSRQPONMKLJIHGFEDCBA";
	public static final String DEFAULT_NETWORK_INTERFACE = "wlan0";
	public static final int DEFAULT_PUSH_INTERVAL = 15;

	public static final String BULB_CONTROL = "BULB";
	public static final String TEMPERATURE_CONTROL = "TEMP";
	public static final String HUMIDITY_CONTROL = "HUMID";
}
