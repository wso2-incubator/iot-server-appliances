package org.wso2.devicemgt.raspberry.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.exception.AuthenticationException;
import org.wso2.carbon.databridge.commons.exception.DifferentStreamDefinitionAlreadyDefinedException;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.StreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Created by ace on 8/7/15.
 */
public class PushBamData {

    private static final Log log = LogFactory.getLog(PushBamData.class);

    private static DataPublisher dataPublisher = null;
    String streamId = null;

    final AgentConstants constants = new AgentConstants();

    /**
     * Declare the stream
     * @return
     */
    public boolean initializeDataPublisher(){

        try {

            setTrustStoreParams();
            log.info("Initializing BAM data publisher.");
            dataPublisher = new DataPublisher(constants.prop.getProperty("bam.thrift.url"), constants.prop.getProperty("bam.username"), constants.prop.getProperty("bam.password"));

            streamId = dataPublisher.defineStream("{" +
                                                          "  'name':'org.wso2.firealarm.bulb.status.stream'," +
                                                          "  'version':'1.0.0'," +
                                                          "  'nickName': 'Firealarm bulb status stream'," +
                                                          "  'description': 'Some Desc'," +
                                                          "  'metaData':[" +
                                                          "          {'name':'deviceId','type':'STRING'}" +
                                                          "  ]," +
                                                          "  'payloadData':[" +
                                                          "          {'name':'type','type':'STRING'}," +
                                                          "          {'name':'owner','type':'STRING'}," +
                                                          "          {'name':'action','type':'STRING'}" +
                                                          "  ]" +
                                                          "}");

            return true;

        } catch (AgentException e) {
            log.error("Error in agent : "+e.getMessage());
        } catch (MalformedStreamDefinitionException e) {
            log.error("Malformed stream definition : "+e.getMessage());
        } catch (StreamDefinitionException e) {
            log.error("Error in stream definition : "+e.getMessage());
        } catch (DifferentStreamDefinitionAlreadyDefinedException e) {
            log.error("Duplicate stream definition : "+e.getMessage());
        } catch (MalformedURLException e) {
            log.error("Malformed URL : "+e.getMessage());
        } catch (AuthenticationException e) {
            log.error("Error in authentication : "+e.getMessage());
        } catch (TransportException e) {
            log.error("Error in transport : "+e.getMessage());
        }

        return false;
    }

    public void setTrustStoreParams() {
        File filePath = new File(constants.prop.getProperty("bam.trust.store.file.path"));
        if (!filePath.exists()) {
            filePath = new File("resources");
        }
        String trustStore = filePath.getAbsolutePath();
        System.setProperty("javax.net.ssl.trustStore", trustStore + "/client-truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

    }

    /**
     * Push data to BAM
     * @param deviceId
     * @param type
     * @param owner
     * @param event
     * @return
     */
    public boolean publishData(String deviceId, String type, String owner, String event){
        try {

            log.debug("DeviceID : "+deviceId+", Type : "+type+", Owner: "+owner+", Event : "+event);
            dataPublisher.publish(streamId, new Object[]{deviceId}, null, new Object[]{type, owner, event});
//            dataPublisher.stop();
            return true;
        } catch (AgentException e) {
            e.printStackTrace();
        }
        return false;
    }





}
