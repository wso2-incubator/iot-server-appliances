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

package org.wso2.carbon.device.mgt.iot.services;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.wso2.carbon.device.mgt.iot.config.DeviceConfigurationManager;
import org.wso2.carbon.device.mgt.iot.config.DeviceManagementConfig;
import org.wso2.carbon.device.mgt.iot.config.DeviceManagementControllerConfig;
import org.wso2.carbon.device.mgt.iot.config.DeviceManagementSecurityConfig;
import org.wso2.carbon.device.mgt.iot.config.controlqueue.DeviceControlQueueConfig;
import org.wso2.carbon.device.mgt.iot.config.datastore.DeviceDataStoreConfig;
import org.wso2.carbon.device.mgt.iot.devicecontroller.ControlQueueConnector;
import org.wso2.carbon.device.mgt.iot.devicecontroller.DataStoreConnector;
import org.wso2.carbon.device.mgt.iot.exception.DeviceControllerServiceException;
import org.wso2.carbon.device.mgt.iot.utils.ResourceFileLoader;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import java.io.File;
import java.util.HashMap;

//@Path(value = "/DeviceController")
public class DeviceControllerService {

    private static Logger log = Logger.getLogger(DeviceControllerService.class);
    private static DataStoreConnector iotDataStore = null;
    private static ControlQueueConnector iotControlQueue = null;
    private static DeviceDataStoreConfig dataStoreConfig = null;
    private static DeviceControlQueueConfig controlQueueConfig = null;

    static {

        String trustStoreFile = null;
        String trustStorePassword = null;
        File certificateFile = null;

        DeviceManagementConfig config = null;

        try {
            config = DeviceConfigurationManager.getInstance().getFireAlarmMgtConfig();
        } catch (DeviceControllerServiceException ex) {
            log.error(ex.getMessage(), ex);
        }

        if (config != null) {
            /* reading security configurations */
            DeviceManagementSecurityConfig securityConfig = config.getDeviceManagementSecurityConfig();
            trustStoreFile = securityConfig.getClient();
            trustStorePassword = securityConfig.getTrustStorePassword();
            certificateFile = new ResourceFileLoader("/resources/security/" + trustStoreFile).getFile();

            if (certificateFile.exists()) {
                trustStoreFile = certificateFile.getAbsolutePath();
                log.info("Trust Store Path : " + trustStoreFile);

                System.setProperty("javax.net.ssl.trustStore", trustStoreFile);
                System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
            } else {
                log.error("Trust Store not found in path : " + trustStoreFile);
            }

            // controller configurations
            DeviceManagementControllerConfig controllerConfig = config.getFireAlarmManagementControllerConfig();

            // reading data store configurations
            String deviceDataStoreKey = controllerConfig.getDeviceDataStore();
            log.info("Active Data-Store: " + deviceDataStoreKey);

            dataStoreConfig = (DeviceDataStoreConfig) config.getDataStoresMap().get(deviceDataStoreKey);
            if (dataStoreConfig == null) {
                log.error("Error occurred when trying to read data stores configurations");
            }

            //initialization data store
            try {
                String handlerClass = dataStoreConfig.getHandlerClass().trim();
                Class<?> dataStore = DeviceControllerService.class.forName(handlerClass);
                if (DataStoreConnector.class.isAssignableFrom(dataStore)) {
                    iotDataStore = (DataStoreConnector) dataStore.newInstance();
                    iotDataStore.initDataStore();
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
                log.error("Error occurred when trying to initiate data store", ex);
            } catch (DeviceControllerServiceException ex) {
                log.error(ex.getMessage(), ex);
            }

            // reading control queue configurations
            String controlQueueKey = controllerConfig.getDeviceControlQueue();
            controlQueueConfig = (DeviceControlQueueConfig) config.getControlQueuesMap().get(controlQueueKey);
            if (controlQueueConfig == null) {
                log.error("Error occurred when trying to read control queue configurations");
            }

            //initialization control queue
            try {
                String handlerClass = controlQueueConfig.getHandlerClass().trim();
                Class<?> controlQueue = DeviceControllerService.class.forName(handlerClass);
                if (ControlQueueConnector.class.isAssignableFrom(controlQueue)) {
                    iotControlQueue = (ControlQueueConnector) controlQueue.newInstance();
                    iotControlQueue.initControlQueue();
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
                log.error("Error occurred when trying to initiate control queue", ex);
            } catch (DeviceControllerServiceException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    @Path("/pushdata/{owner}/{type}/{id}/{time}/{key}/{value}") @POST
    // @Produces("application/xml")
    public static String pushData(@PathParam("owner") String owner, @PathParam("type") String deviceType,
            @PathParam("id") String deviceId, @PathParam("time") Long time, @PathParam("key") String key,
            @PathParam("value") String value, @HeaderParam("description") String description,
            @Context HttpServletResponse response) {

        HashMap<String, String> deviceDataMap = new HashMap<String, String>();

        deviceDataMap.put("owner", owner);
        deviceDataMap.put("deviceType", deviceType);
        deviceDataMap.put("deviceId", deviceId);
        deviceDataMap.put("time", "" + time);
        deviceDataMap.put("key", key);
        deviceDataMap.put("value", value);
        deviceDataMap.put("description", description);

        //DeviceValidator deviceChecker = new DeviceValidator();

        //DeviceIdentifier dId = new DeviceIdentifier();
        //dId.setId(deviceId);
        //dId.setType(deviceType);

        //		try {
        //			boolean exists = deviceChecker.isExist(owner, dId);
        String result = null;
        //			if (exists) {
        try {
            iotDataStore.publishIoTData(deviceDataMap);
            response.setStatus(HttpStatus.SC_ACCEPTED);
            result = "Data Published Succesfully...";
        } catch (DeviceControllerServiceException e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            result = "Failed to push " + description + " data to dataStore at " + dataStoreConfig.getEndPoint() + ":"
                    + dataStoreConfig.getPort();
        }
        //
        //			}
        //
        return result;
        //
        //		} catch (InstantiationException e) {
        //			response.setStatus(500);
        //			return null;
        //		} catch (IllegalAccessException e) {
        //			response.setStatus(500);
        //			return null;
        //		} catch (ConfigurationException e) {
        //			response.setStatus(500);
        //			return null;
        //		} catch (DeviceCloudException e) {
        //			response.setStatus(500);
        //			return null;
        //		}

    }

    @Path("/setcontrol/{owner}/{type}/{id}/{key}/{value}") @POST public static String setControl(
            @PathParam("owner") String owner, @PathParam("type") String deviceType, @PathParam("id") String deviceId,
            @PathParam("key") String key, @PathParam("value") String value, @Context HttpServletResponse response) {
        HashMap<String, String> deviceControlsMap = new HashMap<String, String>();

        deviceControlsMap.put("owner", owner);
        deviceControlsMap.put("deviceType", deviceType);
        deviceControlsMap.put("deviceId", deviceId);
        deviceControlsMap.put("key", key);
        deviceControlsMap.put("value", value);

        String result = null;
        try {
            iotControlQueue.enqueueControls(deviceControlsMap);
            response.setStatus(HttpStatus.SC_ACCEPTED);
            result = "Controls added to queue succesfully..";
        } catch (DeviceControllerServiceException e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            result = "Failed to enqueue data to queue at " + controlQueueConfig.getEndPoint() + ":" + controlQueueConfig
                    .getPort();
        }
        return result;
    }

    // public static void main(String[] args) {
    //
    // DeviceController myController = new DeviceController();
    // String pushOut =
    // myController.pushData("10.100.7.38", "Arduino", "Shabirmean", "123456",
    // Long.parseLong("234890"), "Sensor", "23", "Testing");
    //
    // String setOut = myController.setControl("Shabirmean", "Arduino",
    // "123456", "13", "HIGH");
    //
    // System.out.println("---------------------------------------");
    // System.out.println("PUSH : " + pushOut);
    // System.out.println("---------------------------------------");
    // System.out.println("SET : " + setOut);
    // }
}
