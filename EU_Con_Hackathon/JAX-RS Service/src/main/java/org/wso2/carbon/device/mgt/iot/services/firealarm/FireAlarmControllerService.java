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

package org.wso2.carbon.device.mgt.iot.services.firealarm;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.config.DeviceConfigurationManager;
import org.wso2.carbon.device.mgt.iot.config.DeviceManagementConfig;
import org.wso2.carbon.device.mgt.iot.config.DeviceManagementControllerConfig;
import org.wso2.carbon.device.mgt.iot.config.controlqueue.DeviceControlQueueConfig;
import org.wso2.carbon.device.mgt.iot.exception.DeviceControllerServiceException;
import org.wso2.carbon.device.mgt.iot.services.DeviceControllerService;
import org.wso2.carbon.device.mgt.iot.services.DeviceJSON;
import org.wso2.carbon.device.mgt.iot.services.MQTTSubscriber;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

//@Path(value = "/FireAlarmController")
public class FireAlarmControllerService {

    private static Logger log = Logger.getLogger(FireAlarmControllerService.class);

    public static final String CONTROL_QUEUE_ENDPOINT;
    public static final HashMap<String, LinkedList<String>> replyMsgQueue;
    public static final HashMap<String, LinkedList<String>> internalControlsQueue;
    private static MQTTSubscriber mqttSubscriber;

    static {
        DeviceManagementConfig config = null;

        try {
            config = DeviceConfigurationManager.getInstance().getFireAlarmMgtConfig();
        } catch (DeviceControllerServiceException ex) {
            log.error(ex.getMessage());
        }

        if (config != null) {
            // controller configurations
            DeviceManagementControllerConfig controllerConfig = config.getFireAlarmManagementControllerConfig();

            // reading control queue configurations
            String controlQueueKey = controllerConfig.getDeviceControlQueue();
            DeviceControlQueueConfig controlQueueConfig = config.getControlQueuesMap().get(controlQueueKey);
            if (controlQueueConfig == null) {
                log.error("Error occurred when trying to read control queue configurations");
            }

            String mqttUrl = controlQueueConfig.getEndPoint();
            String mqttPort = controlQueueConfig.getPort();
            CONTROL_QUEUE_ENDPOINT = mqttUrl + ":" + mqttPort;
            log.info("CONTROL_QUEUE_ENDPOINT Successfully initialized.");
        } else {
            CONTROL_QUEUE_ENDPOINT = null;
            log.error("CONTROL_QUEUE_ENDPOINT initialization failed.");
        }

        replyMsgQueue = new HashMap<String, LinkedList<String>>();
        internalControlsQueue = new HashMap<String, LinkedList<String>>();
    }

    /**
     * @param mqttSubscriber
     *            the mqttSubscriber to set
     */
    public void setMqttSubscriber(MQTTSubscriber mqttSubscriber) {
        this.mqttSubscriber = mqttSubscriber;
        try {
            mqttSubscriber.subscribe();
        } catch (DeviceManagementException e) {
            log.error(e.getErrorMessage());
        }
    }

    public static MQTTSubscriber getMQTTSubscriber() {
        return mqttSubscriber;
    }

    /*    Service to switch "ON" and "OFF" the FireAlarm bulb
               Called by an external client intended to control the FireAlarm bulb */
    @Path("/switchbulb") @POST public String switchBulb(@HeaderParam("owner") String owner,
            @HeaderParam("deviceId") String deviceId, @Context HttpServletResponse response) {
        String result = null;
        result = DeviceControllerService.setControl(owner, "FireAlarm", deviceId, "BULB", "IN", response);
        return result;
    }

    /*    Service to read the temperature from the FireAlarm temperature sensor
                   Called by an external client intended to get the current temperature */
    @Path("/readtemperature") @GET public String readTempearature(@HeaderParam("owner") String owner,
            @HeaderParam("deviceId") String deviceId, @Context HttpServletResponse response) {
        String result = null;
        result = DeviceControllerService.setControl(owner, "FireAlarm", deviceId, "TEMPERATURE", "IN", response);
        return result;
    }

    /*    Service to toggle the FireAlarm fan between "ON" and "OFF"
               Called by an external client intended to control the FireAlarm fan */
    @Path("/togglefan") @POST public String switchFan(@HeaderParam("owner") String owner,
            @HeaderParam("deviceId") String deviceId, @Context HttpServletResponse response) {
        String result = null;
        result = DeviceControllerService.setControl(owner, "FireAlarm", deviceId, "FAN", "IN", response);
        return result;
    }

    /*    Service to poll the control-queue for the controls sent to the FireAlarm
               Called by the FireAlarm device  */
    @Path("/readcontrols/{owner}/{deviceId}") @GET public String readControls(@PathParam("owner") String owner,
            @PathParam("deviceId") String deviceId, @Context HttpServletResponse response) {
        String result = null;
        LinkedList<String> deviceControlList = internalControlsQueue.get(deviceId);

        if (deviceControlList == null) {
            result = "No controls have been set for device " + deviceId + " of owner " + owner;
            response.setStatus(HttpStatus.SC_NO_CONTENT);
        } else {
            try {
                result = deviceControlList.remove();
                response.setStatus(HttpStatus.SC_ACCEPTED);
                response.addHeader("Control", result);
            } catch (NoSuchElementException ex) {
                result = "There are no more controls for device " + deviceId + " of owner " + owner;
                response.setStatus(HttpStatus.SC_NO_CONTENT);
            }
        }
        log.info(result);
        return result;
    }

    /*    Service to send back the replies for the controls sent to the FireAlarm
           Called by the FireAlarm device  */
    @Path("/reply") @POST @Consumes(MediaType.APPLICATION_JSON) public String reply(final DeviceJSON replyMsg,
            @Context HttpServletResponse response) {
        String result = null;
        result = DeviceControllerService
                .setControl(replyMsg.owner, "FireAlarm", replyMsg.deviceId, replyMsg.reply, "OUT", response);
        return result;
    }

    /*    Service to push all the sensor data collected by the FireAlarm
           Called by the FireAlarm device  */
    @Path("/pushalarmdata") @POST @Consumes(MediaType.APPLICATION_JSON) public String pushAlarmData(
            final DeviceJSON dataMsg, @Context HttpServletResponse response) {
        String result = null;

        String sensorValues = dataMsg.value;
        log.info("Recieved Sensor Data Values: " + sensorValues);

        String sensors[] = sensorValues.split("-");

        if (sensors.length == 3) {
            String temperature = sensors[0];
            String bulb = sensors[1];
            String fan = sensors[2];

            sensorValues = "Temperature:" + temperature + "C\tBulb Status:" + bulb + "\t\tFan Status:" + fan;
            log.info(sensorValues);

            result = DeviceControllerService
                    .pushData(dataMsg.owner, "FireAlarm", dataMsg.deviceId, System.currentTimeMillis(), "DeviceData",
                            temperature, "TEMP", response);

            if (response.getStatus() != HttpStatus.SC_ACCEPTED) {
                return result;
            }

            result = DeviceControllerService
                    .pushData(dataMsg.owner, "FireAlarm", dataMsg.deviceId, System.currentTimeMillis(), "DeviceData",
                            bulb, "BULB", response);

            if (response.getStatus() != HttpStatus.SC_ACCEPTED) {
                return result;
            }

            result = DeviceControllerService
                    .pushData(dataMsg.owner, "FireAlarm", dataMsg.deviceId, System.currentTimeMillis(), "DeviceData",
                            fan, "FAN", response);

        } else {
            result = DeviceControllerService
                    .pushData(dataMsg.owner, "FireAlarm", dataMsg.deviceId, System.currentTimeMillis(), "DeviceData",
                            dataMsg.value, dataMsg.reply, response);
            return result;
        }

        return result;
    }
}
