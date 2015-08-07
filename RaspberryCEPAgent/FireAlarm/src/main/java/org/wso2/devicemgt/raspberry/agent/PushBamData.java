package org.wso2.devicemgt.raspberry.agent;

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

    private static DataPublisher dataPublisher = null;
    String streamId = null;

    final AgentConstants constants = new AgentConstants();


    public boolean initializeDataPublisher(){

        try {

            setTrustStoreParams();
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
            e.printStackTrace();
        } catch (MalformedStreamDefinitionException e) {
            e.printStackTrace();
        } catch (StreamDefinitionException e) {
            e.printStackTrace();
        } catch (DifferentStreamDefinitionAlreadyDefinedException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (TransportException e) {
            e.printStackTrace();
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

    public boolean publishData(String deviceId, String type, String owner, String event){
        try {

            System.out.println("########### "+event);
            dataPublisher.publish(streamId, new Object[]{deviceId}, null, new Object[]{type, owner, event});
//            dataPublisher.stop();
            return true;
        } catch (AgentException e) {
            e.printStackTrace();
        }
        return false;
    }





}
