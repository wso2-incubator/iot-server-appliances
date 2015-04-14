package org.wso2.iot.platform.devices;

import java.io.File;
import java.net.MalformedURLException;

import javax.ws.rs.*;

import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.exception.AuthenticationException;
import org.wso2.carbon.databridge.commons.exception.DifferentStreamDefinitionAlreadyDefinedException;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.StreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

@Path("")
public class ConnectedDevice {
	Logger log = Logger.getLogger("org.wso2.iot.platform.devices");
	static final String dataStoreEndpoint = "tcp://localhost:7613";
	static final String dataStoreUsername = "admin";
	static final String dataStorePassword = "admin";


	@Path("/pushdata/{ip}/{owner}/{type}/{mac}/{time}/{pin}/{value}")
	@POST
	// @Produces("application/xml")
	public String pushData(@PathParam("ip") String ipAdd, @PathParam("type") String deviceType,
	                       @PathParam("owner") String owner, @PathParam("mac") String macAddress,
	                       @PathParam("pin") String pin, @PathParam("time") String time,
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
			dataPublisher.publish(devicePinDataStream, System.currentTimeMillis(),
			                      new Object[] { ipAdd, deviceType, owner, Long.parseLong(time) }, null,
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


	private static void setTrustStoreParams() {
		File filePath = new File("resources");
		if (!filePath.exists()) {
			filePath = new File("repository/resources/security");
		}
		String trustStore = filePath.getAbsolutePath();
		System.setProperty("javax.net.ssl.trustStore", trustStore + "/client-truststore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

	}



	public static void main(String[] args) {
		ConnectedDevice TestObject = new ConnectedDevice();

		 String out =
		 TestObject.pushData("localhost", "arduino", "smean", "123456", "Today", "13",
		 "HIGH",
		 "Test");
		 System.out.println("PushData : " + out);


	}
}