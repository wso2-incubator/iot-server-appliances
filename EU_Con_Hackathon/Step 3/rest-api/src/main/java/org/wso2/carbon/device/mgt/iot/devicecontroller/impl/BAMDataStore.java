/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.device.mgt.iot.devicecontroller.impl;

import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.exception.*;
import org.wso2.carbon.device.mgt.iot.arduino.firealarm.constants.FireAlarmConstants;
import org.wso2.carbon.device.mgt.iot.config.DeviceConfigurationManager;
import org.wso2.carbon.device.mgt.iot.config.DeviceManagementConfig;
import org.wso2.carbon.device.mgt.iot.config.DeviceManagementControllerConfig;
import org.wso2.carbon.device.mgt.iot.config.datastore.DeviceDataStoreConfig;
import org.wso2.carbon.device.mgt.iot.devicecontroller.DataStoreConnector;
import org.wso2.carbon.device.mgt.iot.exception.DeviceControllerServiceException;

import java.net.MalformedURLException;
import java.util.HashMap;

/**
 * @author smean-MAC
 *
 */
public class BAMDataStore implements DataStoreConnector {

    Logger log = Logger.getLogger(BAMDataStore.class);

    private String DATASTORE_ENDPOINT = "";
    private String DATASTORE_USERNAME = "";
    private String DATASTORE_PASSWORD = "";

    private DataPublisher BAM_DATA_PUBLISHER = null;
    private String DEVICE_DATA_STREAM = null;

    public BAMDataStore() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.wso2.iot.device.controller.DataStoreConnector#initDataStore()
     */
    @Override public void initDataStore() throws DeviceControllerServiceException {
        String dataStore = null;
        String bamUrl = "";
        String bamPort = "";

        DeviceManagementConfig config = null;
        try {
            config = DeviceConfigurationManager.getInstance().getFireAlarmMgtConfig();
            // controller configurations
            DeviceManagementControllerConfig controllerConfig = config.getFireAlarmManagementControllerConfig();
            dataStore = controllerConfig.getDeviceDataStore();

            DeviceDataStoreConfig dataStoreConfig = config.getDataStoresMap().get(dataStore);

            bamUrl = dataStoreConfig.getEndPoint();
            bamPort = dataStoreConfig.getPort();

            DATASTORE_ENDPOINT = bamUrl + ":" + bamPort;
            DATASTORE_USERNAME = dataStoreConfig.getUserName();
            DATASTORE_PASSWORD = dataStoreConfig.getPassword();

            log.info("DATASTORE_ENDPOINT : " + DATASTORE_ENDPOINT);
        } catch (DeviceControllerServiceException ex) {
            String error = "Error occurred when trying to read configurations file: firealarm-config.xml";
            log.error(error, ex);
            throw new DeviceControllerServiceException(error, ex);
        }

        try {
            BAM_DATA_PUBLISHER = new DataPublisher(DATASTORE_ENDPOINT, DATASTORE_USERNAME, DATASTORE_PASSWORD);
            log.info("DATA PUBLISHER created for endpoint " + DATASTORE_ENDPOINT);
        } catch (MalformedURLException | AgentException | AuthenticationException
                | TransportException e) {
            String error = "Error creating DataPublisher for Endpoint: " + DATASTORE_ENDPOINT +
                    " with credentials, USERNAME-" + DATASTORE_USERNAME + " and PASSWORD-" +
                    DATASTORE_PASSWORD + ": ";
            log.error(error, e);
            throw new DeviceControllerServiceException(error, e);
        }

        try {
            DEVICE_DATA_STREAM = BAM_DATA_PUBLISHER.defineStream(
                    "{" + "'name':'org_wso2_iot_devices_data'," + "'version':'1.0.0',"
                            + "'nickName': 'IoT Connected Device Data'," + "'description': 'Data Received from Device',"
                            + "'tags': ['iot', 'embeddedDevice']," + "'metaData':["
                            + "        {'name':'owner','type':'STRING'},"
                            + "        {'name':'deviceType','type':'STRING'},"
                            + "        {'name':'deviceId','type':'STRING'}," + "		{'name':'requestTime','type':'LONG'}"
                            + "]," + "'payloadData':[" + "        {'name':'key','type':'STRING'},"
                            + "        {'name':'value','type':'STRING'},"
                            + "        {'name':'description','type':'STRING'}" + "]" + "}");

            log.info("stream definition ID for data from device pin: " + DEVICE_DATA_STREAM);

        } catch (AgentException | MalformedStreamDefinitionException | StreamDefinitionException
                | DifferentStreamDefinitionAlreadyDefinedException e) {
            String error = "Error in defining default stream for data publisher";
            log.error(error, e);
            throw new DeviceControllerServiceException(error, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.wso2.iot.device.controller.DataStoreConnector#publishIoTData(java
     * .util.HashMap)
     */
    @Override public void publishIoTData(HashMap<String, String> deviceData) throws DeviceControllerServiceException {

        String logMsg = "";
        String owner = deviceData.get("owner");
        String deviceType = deviceData.get("deviceType");
        String deviceId = deviceData.get("deviceId");
        String time = deviceData.get("time");
        String key = deviceData.get("key");
        String value = deviceData.get("value");
        String description = deviceData.get("description");

        try {
            switch (description) {
            case "TEMP":
                if (log.isDebugEnabled()) {
                    log.info("Stream definition set to Temperature");
                }
                DEVICE_DATA_STREAM = BAM_DATA_PUBLISHER.defineStream(BAMStreamDefinitions.TEMPERATURE_STREAM_DEFINITION);
                break;
            case "MOTION":
                if (log.isDebugEnabled()) {
                    log.info("Stream definition set to Motion (PIR)");
                }
                DEVICE_DATA_STREAM = BAM_DATA_PUBLISHER.defineStream(BAMStreamDefinitions.MOTION_STREAM_DEFINITION);
                break;
            case "SONAR":
                if (log.isDebugEnabled()) {
                    log.info("Stream definition set to Sonar");
                }
                DEVICE_DATA_STREAM = BAM_DATA_PUBLISHER.defineStream(BAMStreamDefinitions.SONAR_STREAM_DEFINITION);
                break;
            case "LIGHT":
                if (log.isDebugEnabled()) {
                    log.info("Stream definition set to Light");
                }
                DEVICE_DATA_STREAM = BAM_DATA_PUBLISHER.defineStream(BAMStreamDefinitions.LIGHT_STREAM_DEFINITION);
                break;
            case "BULB":
                if (log.isDebugEnabled()) {
                    log.info("Stream definition set to Bulb Status");
                }
                DEVICE_DATA_STREAM = BAM_DATA_PUBLISHER.defineStream(BAMStreamDefinitions.BULB_STREAM_DEFINITION);
                break;
            case "FAN":
                if (log.isDebugEnabled()) {
                    log.info("Stream definition set to Fan Status");
                }
                DEVICE_DATA_STREAM = BAM_DATA_PUBLISHER.defineStream(BAMStreamDefinitions.FAN_STREAM_DEFINITION);
                break;
            }
        } catch (AgentException | MalformedStreamDefinitionException | StreamDefinitionException
                | DifferentStreamDefinitionAlreadyDefinedException e) {
            String error = "Error in defining fire-alarm specific streams for data publisher";
            log.error(error, e);
            throw new DeviceControllerServiceException(error, e);
        }

        try {
            if (deviceType.equalsIgnoreCase("FireAlarm") | deviceType.equalsIgnoreCase("SenseBot")) {
                if (log.isDebugEnabled()) {
                    log.info("Publishing FireAlarm specific data");
                }
                BAM_DATA_PUBLISHER.publish(DEVICE_DATA_STREAM, System.currentTimeMillis(),
                        new Object[] { owner, deviceType, deviceId, Long.parseLong(time) }, null,
                        new Object[] { value });

                logMsg =
                        "event published to devicePinDataStream\n" + "\tOwner: " + owner + "\tDeviceType: " + deviceType
                                + "\n" + "\tDeviceId: " + deviceId + "\tTime: " + time + "\n" + "\tDescription: "
                                + description + "\n" + "\tKey: " + key + "\tValue: " + value + "\n";

            } else {
                if (log.isDebugEnabled()) {
                    log.info("Publishing common device specific data");
                }
                BAM_DATA_PUBLISHER.publish(DEVICE_DATA_STREAM, System.currentTimeMillis(),
                        new Object[] { owner, deviceType, deviceId, Long.parseLong(time) }, null,
                        new Object[] { key, value, description });

                logMsg =
                        "event published to devicePinDataStream\n" + "\tOwner: " + owner + "\tDeviceType: " + deviceType
                                + "\n" + "\tDeviceId: " + deviceId + "\tTime: " + time + "\n" + "\tDescription: "
                                + description + "\n" + "\tKey: " + key + "\tValue: " + value + "\n";

            }

            if (log.isDebugEnabled()) {
                log.info(logMsg);
            }

        } catch (AgentException e) {
            String error = "Error while publishing device pin data";
            log.error(error, e);
            throw new DeviceControllerServiceException(error, e);
        }
    }

	/*
     *
	 * ==========================================================
	 * // Have to define the stream definition in the BAM tbox
	 * 
	 * ==========================================================
	 */

    // public static void main(String[] args) {
    //
    // File file =
    // new File(
    // "/Users/smean-MAC/Documents/WSO2Git/device-cloud/WSO2ConnectedDevices/src/main/webapp/resources/security/client-truststore.jks");
    // System.out.println(file);
    //
    // if (file.exists()) {
    // String trustStore = file.getAbsolutePath();
    // System.out.println(trustStore);
    // System.setProperty("javax.net.ssl.trustStore", trustStore);
    // System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
    // }
    //
    // HashMap<String, String> myMap = new HashMap<String, String>();
    // myMap.put("ipAdd", "192.168.1.216");
    // myMap.put("deviceType", "Arduino");
    // myMap.put("owner", "Smeansbeer");
    // myMap.put("macAddress", "123456");
    // myMap.put("time", "" + System.nanoTime());
    // myMap.put("key", "TempSensor");
    // myMap.put("value", "123");
    // myMap.put("description", "TetsCase");
    //
    // BAMDataStore newinst = new BAMDataStore();
    // System.out.println(newinst.initDataStore());
    // System.out.println(newinst.publishIoTData(myMap));
    // }
}
