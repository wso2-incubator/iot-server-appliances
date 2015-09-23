package org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.internal;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.constants.AgentConstants;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.exception
		.AgentCoreOperationException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AgentCoreOperations {

	private static final Logger log = LoggerFactory.getLogger(AgentCoreOperations.class);

	/**
	 * This method reads the agent specific configurations for the device from the "deviceConfigs.properties" file found at /repository/conf folder
	 * If the properties file is not found in the specified path, then the configuration values are set to the default ones in the 'AgentConstants' class
	 * @return Object of type 'AgentConfigurations' which contains all the necessary configuration attributes
	 */
	public static AgentConfigurations readIoTServerConfigs() {
		AgentConfigurations iotServerConfigs = new AgentConfigurations();
		Properties properties = new Properties();
		InputStream propertiesInputStream = null;
		String propertiesFileName = AgentConstants.AGENT_PROPERTIES_FILE_NAME;

		try {
			propertiesInputStream = new FileInputStream(AgentConstants.PROPERTIES_FILE_PATH + propertiesFileName);;

			if(propertiesInputStream == null){
				log.error(AgentConstants.LOG_APPENDER + "Unable to find " + propertiesFileName + " file at: " + AgentConstants.PROPERTIES_FILE_PATH);
				log.warn(AgentConstants.LOG_APPENDER + "Default Values are being set to all Agent specific configurations");
				iotServerConfigs.setDeviceOwner(AgentConstants.DEFAULT_DEVICE_OWNER);
				iotServerConfigs.setDeviceId(AgentConstants.DEFAULT_DEVICE_ID);
				iotServerConfigs.setIotServerEndPoint(AgentConstants.DEFAULT_IOT_SERVER_EP);
				iotServerConfigs.setMqttBrokerEndPoint(AgentConstants.DEFAULT_MQTT_BROKER_EP);
				iotServerConfigs.setXmppServerEndPoint(AgentConstants.DEFAULT_XMPP_SERVER_EP);
				iotServerConfigs.setAuthenticationMethod(AgentConstants.DEFAULT_AUTH_METHOD);
				iotServerConfigs.setAuthenticationToken(AgentConstants.DEFAULT_AUTH_TOKEN);
				iotServerConfigs.setRefreshToken(AgentConstants.DEFAULT_REFRESH_TOKEN);
				iotServerConfigs.setNetworkInterface(AgentConstants.DEFAULT_NETWORK_INTERFACE);
				iotServerConfigs.setDataPushInterval(AgentConstants.DEFAULT_PUSH_INTERVAL);
			}

			//load a properties file from class path, inside static method
			properties.load(propertiesInputStream);

			iotServerConfigs.setDeviceOwner(properties.getProperty(AgentConstants.DEVICE_OWNER_PROPERTY));
			iotServerConfigs.setDeviceId(properties.getProperty(AgentConstants.DEVICE_ID_PROPERTY));
			iotServerConfigs.setIotServerEndPoint(properties.getProperty(AgentConstants.IOT_SERVER_EP_PROPERTY));
			iotServerConfigs.setMqttBrokerEndPoint(properties.getProperty(AgentConstants.MQTT_BROKER_EP_PROPERTY));
			iotServerConfigs.setXmppServerEndPoint(properties.getProperty(AgentConstants.XMPP_SERVER_EP_PROPERTY));
			iotServerConfigs.setAuthenticationMethod(properties.getProperty(AgentConstants.AUTH_METHOD_PROPERTY));
			iotServerConfigs.setAuthenticationToken(properties.getProperty(AgentConstants.AUTH_TOKEN_PROPERTY));
			iotServerConfigs.setRefreshToken(properties.getProperty(AgentConstants.REFRESH_TOKEN_PROPERTY));
			iotServerConfigs.setNetworkInterface(properties.getProperty(AgentConstants.NETWORK_INTERFACE_PROPERTY));
			iotServerConfigs.setDataPushInterval(Integer.parseInt(properties.getProperty(AgentConstants.PUSH_INTERVAL_PROPERTY)));

			log.info(AgentConstants.LOG_APPENDER + "Device Owner: " + iotServerConfigs.getDeviceOwner());
			log.info(AgentConstants.LOG_APPENDER + "Device ID: " + iotServerConfigs.getDeviceId());
			log.info(AgentConstants.LOG_APPENDER + "IoT Server EndPoint: " + iotServerConfigs.getIotServerEndPoint());
			log.info(AgentConstants.LOG_APPENDER + "MQTT Broker EndPoint: " + iotServerConfigs.getMqttBrokerEndPoint());
			log.info(AgentConstants.LOG_APPENDER + "XMPP Server EndPoint: " + iotServerConfigs.getXmppServerEndPoint());
			log.info(AgentConstants.LOG_APPENDER + "Authentication Method: " + iotServerConfigs.getAuthenticationMethod());
			log.info(AgentConstants.LOG_APPENDER + "Authentication Token: " + iotServerConfigs.getAuthenticationToken());
			log.info(AgentConstants.LOG_APPENDER + "Refresh Token: " + iotServerConfigs.getRefreshToken());
			log.info(AgentConstants.LOG_APPENDER + "Network Interface: " + iotServerConfigs.getNetworkInterface());
			log.info(AgentConstants.LOG_APPENDER + "Data Push Interval: " + iotServerConfigs.getDataPushInterval());

		} catch (IOException ex) {
			log.error(AgentConstants.LOG_APPENDER + "Error occurred whilst trying to fetch '" + propertiesFileName + "' from: " + AgentConstants.PROPERTIES_FILE_PATH);
		} finally{
			if(propertiesInputStream != null){
				try {
					propertiesInputStream.close();
				} catch (IOException e) {
					log.error(AgentConstants.LOG_APPENDER + "Error occurred whilst trying to close InputStream resource used to read the '" + propertiesFileName + "' file");
				}
			}
		}
		return iotServerConfigs;
	}


	/**
	 * This method constructs the URL patterns for each of the API Endpoints called by the device agent
	 * Ex: Register API, Push-Data API
	 * @throws AgentCoreOperationException Throws exception if any error occurs whilst trying to retrieve the deviceIP
	 */
	public static void initializeHTTPEndPoints() throws AgentCoreOperationException {
		String deviceIPAddress = getDeviceIP(AgentDataHolder.getInstance().getAgentConfigurations().getNetworkInterface());
		AgentDataHolder.getInstance().setDeviceIPAddress(deviceIPAddress);

		String iotServerEndpoint = AgentConstants.HTTP_PREFIX + AgentDataHolder.getInstance().getAgentConfigurations().getIotServerEndPoint();
		AgentDataHolder.getInstance().setIotServerEndPoint(iotServerEndpoint);

		String deviceControllerAPIEndPoint = iotServerEndpoint + AgentConstants.DEVICE_CONTROLLER_API_EP;
		AgentDataHolder.getInstance().setDeviceControllerAPIEndPoint(deviceControllerAPIEndPoint);

		String registerEndpointURL = deviceControllerAPIEndPoint + AgentConstants.DEVICE_REGISTER_API_EP;
		AgentDataHolder.getInstance().setDeviceIPRegistrationEP(registerEndpointURL);

		String pushDataEndPointURL = deviceControllerAPIEndPoint + AgentConstants.DEVICE_PUSH_TEMPERATURE_API_EP;
		AgentDataHolder.getInstance().setPushDataAPIEndPoint(pushDataEndPointURL);

		log.info(AgentConstants.LOG_APPENDER + "Device IP Address: " + deviceIPAddress);
		log.info(AgentConstants.LOG_APPENDER + "IoT Server EndPoint: " + iotServerEndpoint);
		log.info(AgentConstants.LOG_APPENDER + "IoT Server's Device Controller API Endpoint: " + deviceControllerAPIEndPoint);
		log.info(AgentConstants.LOG_APPENDER + "DeviceIP Registration EndPoint: " + registerEndpointURL);
		log.info(AgentConstants.LOG_APPENDER + "Push-Data API EndPoint: " + pushDataEndPointURL);
	}


	/**
	 * This method calls the "Register-API" of the IoT Server in order to register the device's IP against its ID
	 * @param deviceOwner	The owner of the device by whose name the agent was downloaded. (Read from configuration file)
	 * @param deviceID		The deviceId that is auto-generated whilst downloadng the agent. (Read from configuration file)
	 * @return The status code of the HTTP-Post call to the Register-API of the IoT-Server
	 * @throws AgentCoreOperationException Throws for errors that occur when an HTTPConnection session is created
	 */
	public static int registerDeviceIP(String deviceOwner, String deviceID)
			throws AgentCoreOperationException {
		int responseCode = -1;

		String deviceIPRegistrationEP = AgentDataHolder.getInstance().getDeviceIPRegistrationEP();
		String deviceIPAddress = AgentDataHolder.getInstance().getDeviceIPAddress();
		String registerEndpointURLString = deviceIPRegistrationEP + File.separator + deviceOwner + File.separator + deviceID + File.separator + deviceIPAddress;

		if (log.isDebugEnabled()) {
			log.debug(AgentConstants.LOG_APPENDER + "DeviceIP Registration EndPoint: " + registerEndpointURLString);
		}

		HttpURLConnection httpConnection = getHttpConnection(registerEndpointURLString);

		try {
			httpConnection.setRequestMethod(AgentConstants.HTTP_POST);
			httpConnection.setRequestProperty("Authorization", "Bearer " + AgentDataHolder.getInstance().getAgentConfigurations().getAuthenticationToken());
			httpConnection.setDoOutput(true);
			responseCode = httpConnection.getResponseCode();

		} catch (ProtocolException exception) {
			String errorMsg = AgentConstants.LOG_APPENDER + "Protocol specific error occurred when trying to set method to " + AgentConstants.HTTP_POST + " for:" + registerEndpointURLString;
			log.error(errorMsg);
			throw new AgentCoreOperationException(errorMsg, exception);
		} catch (IOException exception) {
			String errorMsg = AgentConstants.LOG_APPENDER + "An IO error occurred whilst trying to get the response code from: " + registerEndpointURLString + " for a " + AgentConstants.HTTP_POST + " method.";
			log.error(errorMsg);
			throw new AgentCoreOperationException(errorMsg, exception);
		}

		log.info(AgentConstants.LOG_APPENDER + "DeviceIP - " + deviceIPAddress + ", registration with IoT Server at : " + AgentDataHolder.getInstance().getIotServerEndPoint() + " returned status " + responseCode);
		return responseCode;
	}


	/**
	 * This method is used to push device data to the IoT-Server via an HTTP invocation to the API.
	 * Invocation of this method calls its overloaded-method with a push-interval equal to that of the default value from "AgentConstants" class
	 * @param deviceOwner The owner of the device by whose name the agent was downloaded. (Read from configuration file)
	 * @param deviceID The deviceId that is auto-generated whilst downloadng the agent. (Read from configuration file)
	 */
	public static void pushDeviceData(final String deviceOwner, final String deviceID){
		pushDeviceData(deviceOwner, deviceID, AgentConstants.DEFAULT_PUSH_INTERVAL);
	}

	/**
	 * This is an overloaded method that pushes device-data to the IoT-Server at given time intervals
	 * @param deviceOwner The owner of the device by whose name the agent was downloaded. (Read from configuration file)
	 * @param deviceID The deviceId that is auto-generated whilst downloadng the agent. (Read from configuration file)
	 * @param interval The time interval between every successive data-push attempts. (Set initially by the startup script and read from the configuration file)
	 */
	public static void pushDeviceData(final String deviceOwner, final String deviceID, int interval){
		final String pushDataEndPointURL = AgentDataHolder.getInstance().getPushDataAPIEndPoint();

		if (log.isDebugEnabled()) {
			log.info(AgentConstants.LOG_APPENDER + "PushData EndPoint: " + pushDataEndPointURL);
		}

		Runnable pushDataThread = new Runnable() {
			public void run() {
				int responseCode = -1;
				String pushDataPayload = null;
				HttpURLConnection httpConnection = null;

				try {
					httpConnection = getHttpConnection(pushDataEndPointURL);
					httpConnection.setRequestMethod(AgentConstants.HTTP_POST);
					httpConnection.setRequestProperty("Authorization", "Bearer " + AgentDataHolder.getInstance().getAgentConfigurations().getAuthenticationToken());
					httpConnection.setRequestProperty("Content-Type", AgentConstants.APPLICATION_JSON_TYPE);

					pushDataPayload = String.format(AgentConstants.PUSH_DATA_PAYLOAD, deviceOwner, deviceID, AgentDataHolder.getInstance().getDeviceIPAddress(), AgentDataHolder.getInstance().getAgentOperationManager().getTemperature());

					if (log.isDebugEnabled()) {
						log.debug(AgentConstants.LOG_APPENDER + "Push Data Payload is: " + pushDataPayload);
					}

					httpConnection.setDoOutput(true);
					DataOutputStream dataOutPutWriter = new DataOutputStream(httpConnection.getOutputStream());
					dataOutPutWriter.writeBytes(pushDataPayload);
					dataOutPutWriter.flush();
					dataOutPutWriter.close();

					responseCode = httpConnection.getResponseCode();
					httpConnection.disconnect();

				} catch (ProtocolException exception) {
					String errorMsg = AgentConstants.LOG_APPENDER + "Protocol specific error occurred when trying to set method to " + AgentConstants.HTTP_POST + " for:" + pushDataEndPointURL;
					log.error(errorMsg);
				} catch (IOException exception) {
					String errorMsg = AgentConstants.LOG_APPENDER + "An IO error occurred whilst trying to get the response code from: " + pushDataEndPointURL + " for a " + AgentConstants.HTTP_POST + " method.";
					log.error(errorMsg);
				} catch (AgentCoreOperationException exception) {
					log.error(AgentConstants.LOG_APPENDER + "Error encountered whilst trying to create HTTP-Connection to IoT-Server EP at: " + pushDataEndPointURL);
				}

				if (responseCode == HttpStatus.CONFLICT_409 || responseCode == HttpStatus.PRECONDITION_FAILED_412) {
					log.warn(AgentConstants.LOG_APPENDER + "DeviceIP is being Re-Registered due to Push-Data failure with response code: " + responseCode);
					try {
						registerDeviceIP(deviceOwner, deviceID);
					} catch (AgentCoreOperationException exception) {
						log.error(AgentConstants.LOG_APPENDER + "Error encountered whilst trying to Re-Register the Device's IP");
					}
				} else if (responseCode != HttpStatus.NO_CONTENT_204){
					log.error(AgentConstants.LOG_APPENDER + "Status Code: " + responseCode + " encountered whilst trying to Push-Device-Data to IoT Server at: " + AgentDataHolder.getInstance().getPushDataAPIEndPoint());
				}

				if (log.isDebugEnabled()) {
					log.debug(AgentConstants.LOG_APPENDER + "Push-Data call with payload - " + pushDataPayload + ", to IoT Server returned status " + responseCode);
				}
			}
		};

		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(pushDataThread, 0, interval, TimeUnit.SECONDS);
	}


	/*------------------------------------------------------------------------------------------*/
	/* 		Utility methods relevant to creating and sending HTTP requests to the Iot-Server 	*/
	/*------------------------------------------------------------------------------------------*/

	/**
	 * This method is used to get the public IP of the device in which the agent is run on
	 * Invocation of this method calls its overloaded-method with the default network-interface name from "AgentConstants" class
	 * @return The public IP Address of the device
	 * @throws AgentCoreOperationException Thrown for errors that occur whilst trying to get details of the given network interface
	 */
	private static String getDeviceIP() throws AgentCoreOperationException {
		return getDeviceIP(AgentConstants.DEFAULT_NETWORK_INTERFACE);
	}

	/**
	 * This is an overloaded method that fetches the public IPv4 address of the given network interface
	 * @param networkInterfaceName The network-interface of whose IPv4 address is to be retrieved
	 * @return	The IP Address iof the device
	 * @throws AgentCoreOperationException Thrown for errors that occur whilst trying to get details of the given network interface
	 */
	private static String getDeviceIP(String networkInterfaceName) throws
																   AgentCoreOperationException {
		String ipAddress = null;
		try {
			Enumeration<InetAddress> interfaceIPAddresses = NetworkInterface.getByName(networkInterfaceName).getInetAddresses();
			for (; interfaceIPAddresses.hasMoreElements(); ) {
				InetAddress ip = interfaceIPAddresses.nextElement();
				ipAddress = ip.getHostAddress().toString();
				if (log.isDebugEnabled()) {
					log.debug(AgentConstants.LOG_APPENDER + "IP Address: " + ipAddress);
				}

				if (validateIPv4(ipAddress)) {
					return ipAddress;
				}
			}
		} catch (SocketException exception) {
			String errorMsg = AgentConstants.LOG_APPENDER + "Error encountered whilst trying to get IP Addresses of the network interface: " + networkInterfaceName + ".\nPlease check whether the name of the network interface used is correct";
			log.error(errorMsg);
			throw new AgentCoreOperationException(errorMsg, exception);
		}
		return ipAddress;
	}


	/**
	 * This method validates whether a specific IP Address is of IPv4 type
	 * @param ipAddress The IP Address which needs to be validated
	 * @return True if it si of IPv4 type and false otherwise
	 */
	private static boolean validateIPv4(String ipAddress) {
		try {
			if (ipAddress == null || ipAddress.isEmpty()) {
				return false;
			}

			String[] parts = ipAddress.split("\\.");
			if (parts.length != 4) {
				return false;
			}

			for (String s : parts) {
				int i = Integer.parseInt(s);
				if ((i < 0) || (i > 255)) {
					return false;
				}
			}
			if (ipAddress.endsWith(".")) {
				return false;
			}

			return true;
		} catch (NumberFormatException nfe) {
			log.warn(AgentConstants.LOG_APPENDER + "The IP Address: " + ipAddress + " could not be validated against IPv4-style");
			return false;
		}
	}


	/**
	 * This is a utility method that creates and returns a HTTP connection object
	 * @param urlString The URL pattern to which the connection needs to be created
	 * @return HTTP Connection object which cn be used to send HTTP requests
	 * @throws AgentCoreOperationException Thrown when errors occur in creating the HTTP connection with the given URL string
	 */
	private static HttpURLConnection getHttpConnection(String urlString) throws
																		 AgentCoreOperationException {

		URL connectionUrl = null;
		HttpURLConnection httpConnection = null;

		try {
			connectionUrl = new URL(urlString);
			httpConnection = (HttpURLConnection) connectionUrl.openConnection();
		} catch (MalformedURLException e) {
			String errorMsg = AgentConstants.LOG_APPENDER + "Error occured whilst trying to form HTTP-URL from string: " + urlString;
			log.error(errorMsg);
			throw new AgentCoreOperationException(errorMsg, e);
		} catch (IOException exception) {
			String errorMsg = AgentConstants.LOG_APPENDER + "Error occured whilst trying to open a connection to: " + connectionUrl.toString();
			log.error(errorMsg);
			throw new AgentCoreOperationException(errorMsg, exception);
		}
		return httpConnection;
	}

	/**
	 * This is a utility method that reads and returns the response from a HTTP connection
	 * @param httpConnection The connection from which a response is expected
	 * @return The response (as a string) from the given HTTP connection
	 * @throws AgentCoreOperationException Thrown for errors that occur whilst reading the response from the connection stream
	 */
	private static String readResponseFromHttpRequest(HttpURLConnection httpConnection)
			throws AgentCoreOperationException {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
		} catch (IOException exception) {
			String errorMsg = AgentConstants.LOG_APPENDER + "There is an issue with connecting the reader to the input stream at: " + httpConnection.getURL();
			log.error(errorMsg);
			throw new AgentCoreOperationException(errorMsg, exception);
		}

		String responseLine;
		StringBuffer completeResponse = new StringBuffer();

		try {
			while ((responseLine = bufferedReader.readLine()) != null) {
				completeResponse.append(responseLine);
			}
		} catch (IOException exception) {
			String errorMsg = AgentConstants.LOG_APPENDER + "Error occured whilst trying read from the connection stream at: " + httpConnection.getURL();
			log.error(errorMsg);
			throw new AgentCoreOperationException(errorMsg, exception);
		}
		try {
			bufferedReader.close();
		} catch (IOException exception) {
			log.error(AgentConstants.LOG_APPENDER + "Could not succesfully close the bufferedReader to the connection at: " + httpConnection.getURL());
		}
		return completeResponse.toString();
	}



	//		OutputStream propertiesOutputStream = null;
//
//		try {
//			propertiesOutputStream = new FileOutputStream("./repository/conf/deviceConfig.properties");
//
//			// set the properties value
//			properties.setProperty("deviceOwner", "Shabirmean");
//			properties.setProperty("deviceId", "12345678");
//			properties.setProperty("iotServerEndPoint", "192.168.2.1");
//			properties.setProperty("mqttBrokerEndPoint", "192.168.2.1");
//			properties.setProperty("xmppServerEndPoint", "192.168.2.1");
//			properties.setProperty("authenticationMethod", "token");
//			properties.setProperty("authenticationToken", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0987654321");
//			properties.setProperty("refreshToken", "1234567890ZYXWVUTSRQPONMKLJIHGFEDCBA");
//
//			// save properties to project root folder
//			properties.store(propertiesOutputStream, "Device-Specific Configurations");
//
//		} catch (IOException ioException) {
//			ioException.printStackTrace();
//		} finally {
//			if (propertiesOutputStream != null) {
//				try {
//					propertiesOutputStream.close();
//				} catch (IOException exception) {
//					exception.printStackTrace();
//				}
//			}
//		}

}
