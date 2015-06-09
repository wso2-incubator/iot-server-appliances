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

package org.wso2.carbon.device.mgt.iot.sensebot.api;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.DeviceController;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.exception.UnauthorizedException;
import org.wso2.carbon.device.mgt.iot.sensebot.api.util.DeviceJSON;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SensebotControllerService {

    private static Log log = LogFactory.getLog(SensebotControllerService.class);

    private static final Map<String, String> deviceIPList = new HashMap<String, String>();

    private static HttpURLConnection httpConn;
    private static final String URL_PREFIX = "http://";
    private static final String FORWARD_URL = "/move/F";
    private static final String BACKWARD_URL = "/move/B";
    private static final String LEFT_URL = "/move/L";
    private static final String RIGHT_URL = "/move/R";
    private static final String STOP_URL = "/move/S";

    @Path("/forward") @POST public String moveForward(@HeaderParam("owner") String owner,
            @HeaderParam("deviceId") String deviceId, @FormParam("ip") String deviceIp,
            @FormParam("port") int deviceServerPort) {

        String result = null;
        result = sendCommand(deviceIp, deviceServerPort, FORWARD_URL);
        return result;
    }

    @Path("/backward") @POST public String moveBackward(@HeaderParam("owner") String owner,
            @HeaderParam("deviceId") String deviceId, @FormParam("ip") String deviceIp,
            @FormParam("port") int deviceServerPort) {
        String result = null;
        result = sendCommand(deviceIp, deviceServerPort, BACKWARD_URL);
        return result;
    }

    @Path("/left") @POST public String turnLeft(@HeaderParam("owner") String owner,
            @HeaderParam("deviceId") String deviceId, @FormParam("ip") String deviceIp,
            @FormParam("port") int deviceServerPort) {
        String result = null;
        result = sendCommand(deviceIp, deviceServerPort, LEFT_URL);
        return result;
    }

    @Path("/right") @POST public String turnRight(@HeaderParam("owner") String owner,
            @HeaderParam("deviceId") String deviceId, @FormParam("ip") String deviceIp,
            @FormParam("port") int deviceServerPort) {
        String result = null;
        result = sendCommand(deviceIp, deviceServerPort, RIGHT_URL);
        return result;
    }

    @Path("/stop") @POST public String stop(@HeaderParam("owner") String owner,
            @HeaderParam("deviceId") String deviceId, @FormParam("ip") String deviceIp,
            @FormParam("port") int deviceServerPort) {
        String result = null;
        result = sendCommand(deviceIp, deviceServerPort, STOP_URL);
        return result;
    }

    @Path("/pushsensordata") @POST @Consumes(MediaType.APPLICATION_JSON) public void pushAlarmData(
            final DeviceJSON dataMsg, @Context HttpServletResponse response) {
        boolean result = false;

        String sensorValues = dataMsg.value;                            //TEMP-PIR-SONAR-LDR
        log.info("Recieved Sensor Data Values: " + sensorValues);

        String sensors[] = sensorValues.split(":");
        try {
            if (sensors.length == 4) {
                String temperature = sensors[0];
                String motion = sensors[1];
                String sonar = sensors[2];
                String light = sensors[3];

                if (sonar.equals("-1")) {
                    sonar = "No Object";
                }

                sensorValues = "Temperature:" + temperature + "C\t\tMotion:" + motion + "\tSonar:" + sonar + "\tLight:"
                        + light;

                if (log.isDebugEnabled())
                    log.debug(sensorValues);

                result = DeviceController
                        .pushData(dataMsg.owner, "SenseBot", dataMsg.deviceId, System.currentTimeMillis(), "DeviceData",
                                temperature, "TEMP");

                if (!result) {
                    response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                    return;
                }

                result = DeviceController
                        .pushData(dataMsg.owner, "SenseBot", dataMsg.deviceId, System.currentTimeMillis(), "DeviceData",
                                motion, "MOTION");

                if (!result) {
                    response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                    return;
                }

                if (!sonar.equals("No Object")) {
                    result = DeviceController
                            .pushData(dataMsg.owner, "SenseBot", dataMsg.deviceId, System.currentTimeMillis(),
                                    "DeviceData", sonar, "SONAR");

                    if (!result) {
                        response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                        return;
                    }

                }

                result = DeviceController
                        .pushData(dataMsg.owner, "SenseBot", dataMsg.deviceId, System.currentTimeMillis(), "DeviceData",
                                light, "LIGHT");

                if (!result) {
                    response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                    return;
                }

            } else {
                response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                return;
            }

        } catch (UnauthorizedException e) {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);

        }

    }

    @Path("/pushtempdata") @POST @Consumes(MediaType.APPLICATION_JSON) public void pushTempData(
            final DeviceJSON dataMsg, @Context HttpServletResponse response) {

        String temperature = dataMsg.value;                            //TEMP-PIR-SONAR-LDR
        if (log.isDebugEnabled())
            log.debug("Recieved Tenperature Data Value: " + temperature + " degrees C");
        try {
            boolean result = DeviceController
                    .pushData(dataMsg.owner, "SenseBot", dataMsg.deviceId, System.currentTimeMillis(), "DeviceData",
                            temperature, "TEMP");

            if (!result) {
                response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                return;
            }

        } catch (UnauthorizedException e) {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);

        }
    }

    @Path("/pushpirdata") @POST @Consumes(MediaType.APPLICATION_JSON) public void pushPIRData(final DeviceJSON dataMsg,
            @Context HttpServletResponse response) {

        String motion = dataMsg.value;                            //TEMP-PIR-SONAR-LDR
        if (log.isDebugEnabled())
            log.debug("Recieved PIR (Motion) Sensor Data Value: " + motion);
        try {
            boolean result = DeviceController
                    .pushData(dataMsg.owner, "SenseBot", dataMsg.deviceId, System.currentTimeMillis(), "DeviceData",
                            motion, "MOTION");

            if (!result) {
                response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                return;
            }

        } catch (UnauthorizedException e) {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);

        }

    }

    @Path("/pushsonardata") @POST @Consumes(MediaType.APPLICATION_JSON) public void pushSonarData(
            final DeviceJSON dataMsg, @Context HttpServletResponse response) {

        String sonar = dataMsg.value;                            //TEMP-PIR-SONAR-LDR

        if (sonar.equals("-1")) {
            if (log.isDebugEnabled())
                log.debug("Recieved a 'No Obstacle' Sonar value. (Means there are no abstacles within 30cm)");
        } else {
            if (log.isDebugEnabled())
                log.debug("Recieved Sonar Sensor Data Value: " + sonar + " cm");
            try {
                boolean result = DeviceController
                        .pushData(dataMsg.owner, "SenseBot", dataMsg.deviceId, System.currentTimeMillis(), "DeviceData",
                                sonar, "SONAR");

                if (!result) {
                    response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                    return;
                }

            } catch (UnauthorizedException e) {
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);

            }

        }

    }

    @Path("/pushlightdata") @POST @Consumes(MediaType.APPLICATION_JSON) public void pushlightData(
            final DeviceJSON dataMsg, @Context HttpServletResponse response) {

        String light = dataMsg.value;                            //TEMP-PIR-SONAR-LDR
        if (log.isDebugEnabled())
            log.debug("Recieved LDR (Light) Sensor Data Value: " + light);

        try {
            boolean result = DeviceController
                    .pushData(dataMsg.owner, "SenseBot", dataMsg.deviceId, System.currentTimeMillis(), "DeviceData",
                            light, "LIGHT");

            if (!result) {
                response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                return;
            }

        } catch (UnauthorizedException e) {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);

        }

    }

    private String sendCommand(String deviceIp, int deviceServerPort, String motionType) {

        if (deviceServerPort == 0) {
            deviceServerPort = 80;
        }

        String urlString = URL_PREFIX + deviceIp + ":" + deviceServerPort + motionType;
        if (log.isDebugEnabled())
            log.debug(urlString);

        String result = "";
        URL url = null;
        int responseCode = 200;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            log.error("Invalid URL: " + urlString);
        }
        try {
            if (url != null) {
                httpConn = (HttpURLConnection) url.openConnection();

                try {
                    httpConn.setRequestMethod(HttpMethod.GET);
                    httpConn.setRequestProperty("User-Agent", "WSO2 Carbon Server");
                    responseCode = httpConn.getResponseCode();
                    result = "" + responseCode + HttpStatus.getStatusText(responseCode) + "(No reply from Robot)";

                    if (log.isDebugEnabled())
                        log.debug("\nSending 'GET' request to URL : " + urlString);
                    if (log.isDebugEnabled())
                        log.debug("Response Code : " + responseCode);
                } catch (ProtocolException e) {
                    log.error("Protocol mismatch exception occured whilst trying to 'GET' resource");
                } catch (IOException e) {
                    log.error(
                            "Error occured whilst reading return code from server. This could be because the server did not return anything");
                    result = "" + responseCode + " " + HttpStatus.getStatusText(responseCode) + "(No reply from Robot)";
                    return result;
                }
            }
        } catch (IOException e) {
            log.error("Error Connecting to HTTP Endpoint at: " + urlString);
        }



        return result;
    }

}
