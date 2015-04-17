package org.wso2.iot.platform.devices;

import java.io.File;
import java.net.MalformedURLException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;

import javax.ws.rs.*;

import org.apache.log4j.Logger;
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.exception.AuthenticationException;
import org.wso2.carbon.databridge.commons.exception.DifferentStreamDefinitionAlreadyDefinedException;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.StreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

@Path("")
public class ConnectedDevice {
    //implements MqttCallback {
    Logger log = Logger.getLogger("org.wso2.iot.platform.devices");

//	MqttClient client;
//	MqttConnectOptions options;

    // static final String mqttEndpoint = "tcp://localhost:1883";
//	static final String mqttEndpoint = "tcp://192.168.1.216:1883";
    static final String dataStoreEndpoint = "tcp://172.16.3.212:7613";
    static final String dataStoreUsername = "admin";
    static final String dataStorePassword = "admin";

    // Database credentials
//	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//	static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/WSO2_Devices";
//	static final String USER = "root";
//	static final String PASS = "123";

    @Path("/pushdata/{ip}/{owner}/{type}/{mac}/{time}/{pin}/{value}")
    @POST
    // @Produces("application/xml")
    public String pushData(@PathParam("ip") String ipAdd, @PathParam("type") String deviceType,
                           @PathParam("owner") String owner, @PathParam("mac") String macAddress,
                           @PathParam("pin") String pin, @PathParam("time") long requestTime,
                           @PathParam("value") String pinValue,
                           @HeaderParam("description") String description) {

        setTrustStoreParams();

        DataPublisher dataPublisher;
        try {
            dataPublisher =
                    new DataPublisher(dataStoreEndpoint, dataStoreUsername,
                                      dataStorePassword);
        } catch (MalformedURLException | AgentException | AuthenticationException
                | TransportException e) {

            log.error("Error creating DataPublisher for Endpoint: " + dataStoreEndpoint +
                      " with credentials, USERNAME-" + dataStoreUsername + " and PASSWORD-" +
                      dataStorePassword + ": ", e);
            return "<connect>" + "<pushdata>" + "<pin>" + pin + "</pin>" + "<value>" + pinValue +
                   "</value>" + "<result>" + false + "</result>" + "</pushdata>" + "</connect>";

        }

        String devicePinDataStream;
        try {
            devicePinDataStream =
                    dataPublisher.defineStream("{"
                                               + "'name':'org_wso2_iot_statistics_device_pin_data',"
                                               + "'version':'1.0.0',"
                                               + "'nickName': 'IoT Connected Device Pin Data',"
                                               + "'description': 'Pin Data Received',"
                                               + "'tags': ['arduino', 'led13'],"
                                               + "'metaData':["
                                               + "        {'name':'ipAdd','type':'STRING'},"
                                               + "        {'name':'deviceType','type':'STRING'},"
                                               + "        {'name':'owner','type':'STRING'},"
                                               + "		{'name':'requestTime','type':'LONG'}"
                                               + "],"
                                               + "'payloadData':["
                                               + "        {'name':'macAddress','type':'STRING'},"
                                               + "        {'name':'pin','type':'STRING'},"
                                               + "        {'name':'pinValue','type':'STRING'},"
                                               + "        {'name':'description','type':'STRING'}"
                                               + "]" + "}");

            log.info("stream definition ID for data from device pin: " + devicePinDataStream);

        } catch (AgentException | MalformedStreamDefinitionException | StreamDefinitionException
                | DifferentStreamDefinitionAlreadyDefinedException e) {

            log.error("Error in defining stream for data publisher: ", e);
            return "<connect>" + "<pushdata>" + "<pin>" + pin + "</pin>" + "<value>" + pinValue +
                   "</value>" + "<result>" + false + "</result>" + "</pushdata>" + "</connect>";

        }

        try {
            System.out.println(devicePinDataStream);
            dataPublisher.publish(devicePinDataStream, System.currentTimeMillis(),
                                  new Object[] { ipAdd, deviceType, owner, requestTime }, null,
                                  new Object[] { macAddress, pin, pinValue, description });

            log.info("event published to devicePinDataStream");

        } catch (AgentException e) {
            log.error("Error while publishing device pin data", e);
            return "<connect>" + "<pushdata>" + "<pin>" + pin + "</pin>" + "<value>" + pinValue +
                   "</value>" + "<result>" + false + "</result>" + "</pushdata>" + "</connect>";
        }

        return "<connect>" + "\n\t<pushdata>" + "\n\t\t<pin>\n\t\t\t" + pin + "\n\t\t</pin>" +
               "\n\t\t<value>\n\t\t\t" + pinValue + "\n\t\t</value>" + "\n\t\t<result>\n\t\t\t" +
               true + "\n\t\t</result>" + "\n\t</pushdata>" + "\n</connect>";
    }

//	@Path("/setcontrol")
//	@POST
//	@Produces("application/xml")
//	public String setControl(@FormParam("owner") String owner, @FormParam("mac") String macAddress,
//	                         @FormParam("pin") String pin, @FormParam("value") String pinValue) {
//
//		log.info("Controls Received: \n\tOwner: " + owner + "\n\tMAC-address: " + macAddress +
//		         "\n\tPin: " + pin + "\n\tValue" + pinValue);
//
//		String clientId = owner + ":" + macAddress;
//		String publishTopic = "wso2/iot/" + owner + "/" + macAddress + "/controlSignal";
//		String payLoad = pin + ":" + pinValue;
//
//		try {
//			client = new MqttClient(mqttEndpoint, clientId);
//			options = new MqttConnectOptions();
//			options.setWill("iotDevice/clienterrors", "crashed".getBytes(), 2, true);
//			client.setCallback(this);
//			client.connect(options);
//
//			log.info("MQTT Client successfully connected to: " + mqttEndpoint +
//			         ", with client ID-" + clientId);
//
//			MqttMessage message = new MqttMessage();
//			message.setPayload(payLoad.getBytes());
//			client.publish(publishTopic, payLoad.getBytes(), 2, true);
//
//			log.info("MQTT Client successfully published to topic: " + publishTopic +
//			         ", with payload - " + payLoad);
//
//			client.disconnect();
//
//			log.info("MQTT Client disconnected from MQTT broker");
//
//		} catch (MqttException e) {
//			log.error("MQTT Client Error", e);
//			return "<connect>" + "<setcontrol>"
//			       + "ERROR: Control message was not published to broker" + "</setcontrol>"
//			       + "</connect>";
//		}
//		return "<connect>" + "<setcontrol>" + payLoad + "</setcontrol>" + "</connect>";
//	}

    private static void setTrustStoreParams() {
        File filePath = new File("src/main/resources");
        if (!filePath.exists()) {
            filePath = new File("resources");
        }
        String trustStore = filePath.getAbsolutePath();
        System.setProperty("javax.net.ssl.trustStore", trustStore + "/client-truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

    }

    // @Path("/dbconnect")
    // @GET
    // @Produces("application/xml")
    // public String readDB() {
    // Connection conn = null;
    // Statement stmt = null;
    // String result = "";
    //
    // try {
    // // STEP 2: Register JDBC driver
    // Class.forName("com.mysql.jdbc.Driver");
    //
    // // STEP 3: Open a connection
    // System.out.println("Connecting to database...");
    // conn = DriverManager.getConnection(DB_URL, USER, PASS);
    //
    // // STEP 4: Execute a query
    // System.out.println("Creating statement...");
    // stmt = conn.createStatement();
    // String sql;
    // sql = "SELECT id, firstname, lastname, email, username FROM user";
    // ResultSet rs = stmt.executeQuery(sql);
    //
    // // STEP 5: Extract data from result set
    // while (rs.next()) {
    // // Retrieve by column name
    // int id = rs.getInt("id");
    // String email = rs.getString("email");
    // String first = rs.getString("firstname");
    // String last = rs.getString("lastname");
    // String user = rs.getString("username");
    //
    // // Display values
    // System.out.println("ID: " + id);
    // System.out.print(", Email: " + email);
    // System.out.print(", FirstName: " + first);
    // System.out.print(", LastName: " + last);
    // System.out.println(", Username: " + user);
    //
    // result = id + " : " + first + " : " + last + " : " + user + " : " +
    // email;
    // }
    // // STEP 6: Clean-up environment
    // rs.close();
    // stmt.close();
    // conn.close();
    // } catch (SQLException se) {
    // // Handle errors for JDBC
    // se.printStackTrace();
    // } catch (Exception e) {
    // // Handle errors for Class.forName
    // e.printStackTrace();
    // } finally {
    // // finally block used to close resources
    // try {
    // if (stmt != null)
    // stmt.close();
    // } catch (SQLException se2) {
    // }// nothing we can do
    // try {
    // if (conn != null)
    // conn.close();
    // } catch (SQLException se) {
    // se.printStackTrace();
    // }// end finally try
    // }// end try
    // System.out.println("Result:" + result);
    // return "<ctofservice>" + "<ctofoutput>" + result + "</ctofoutput>" +
    // "</ctofservice>";
    // }

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.
//	 * Throwable)
//	 */
//	@Override
//	public void connectionLost(Throwable arg0) {
//		// TODO Auto-generated method stub
//
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse
//	 * .paho.client.mqttv3.IMqttDeliveryToken)
//	 */
//	@Override
//	public void deliveryComplete(IMqttDeliveryToken arg0) {
//		// TODO Auto-generated method stub
//
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.
//	 * String, org.eclipse.paho.client.mqttv3.MqttMessage)
//	 */
//	@Override
//	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
//		// TODO Auto-generated method stub
//
//	}

    public static void main(String[] args) {
        ConnectedDevice TestObject = new ConnectedDevice();

        String[] sensorData = {"Gas", "Humidity", "Temperature", "LDR", "Humidity", "Sonar"};

        for (int i = 0; i < sensorData.length; i++) {

            for (int j = 0; j < 10; j++) {

                double randNumber = Math.random();
                double d = randNumber * 1000;

                //Type cast double to int
                int randomInt = (int) d;
                System.out.println(
                        TestObject.pushData("localhost", "arduino", "smean", "123456", sensorData[i], System.currentTimeMillis(),
                                            String.valueOf(randomInt),
                                            ""));
                System.out.println("test");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}