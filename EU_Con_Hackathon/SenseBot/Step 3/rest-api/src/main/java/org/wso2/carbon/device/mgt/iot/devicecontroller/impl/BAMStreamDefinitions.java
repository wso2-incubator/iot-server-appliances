package org.wso2.carbon.device.mgt.iot.devicecontroller.impl;

/**
 * Created by smean-MAC on 5/31/15.
 */
public class BAMStreamDefinitions {
    public final static String TEMPERATURE_STREAM_DEFINITION =
            "{"
                    + "     'name':'org_wso2_iot_devices_temperature',"
                    + "     'version':'1.0.0',"
                    + "     'nickName': 'Temperature Data',"
                    + "     'description': 'Temperature data received from the Device',"
                    + "     'tags': ['iot', 'temperature'],"
                    + "     'metaData': ["
                    + "                     {'name':'owner','type':'STRING'},"
                    + "                     {'name':'deviceType','type':'STRING'},"
                    + "                     {'name':'deviceId','type':'STRING'},"
                    + "                     {'name':'time','type':'LONG'}"
                    + "                 ]," + "     "
                    + "     'payloadData':  ["
                    + "                     {'name':'temperature','type':'STRING'}"
                    + "                     ]"
                    + "}";

    public final static String BULB_STREAM_DEFINITION =
            "{"
                    + "     'name':'org_wso2_iot_devices_bulb',"
                    + "     'version':'1.0.0',"
                    + "     'nickName': 'Bulb Status',"
                    + "     'description': 'State of the bulb attached to a Device',"
                    + "     'tags': ['iot', 'bulb'],"
                    + "     'metaData': ["
                    + "                     {'name':'owner','type':'STRING'},"
                    + "                     {'name':'deviceType','type':'STRING'},"
                    + "                     {'name':'deviceId','type':'STRING'},"
                    + "                     {'name':'time','type':'LONG'}"
                    + "                 ]," + "     "
                    + "     'payloadData':  ["
                    + "                     {'name':'status','type':'STRING'}"
                    + "                     ]"
                    + "}";


    public final static String FAN_STREAM_DEFINITION =
            "{"
                    + "     'name':'org_wso2_iot_devices_fan',"
                    + "     'version':'1.0.0',"
                    + "     'nickName': 'Fan Status',"
                    + "     'description': 'State of the Fan attached to a Device',"
                    + "     'tags': ['iot', 'fan'],"
                    + "     'metaData': ["
                    + "                     {'name':'owner','type':'STRING'},"
                    + "                     {'name':'deviceType','type':'STRING'},"
                    + "                     {'name':'deviceId','type':'STRING'},"
                    + "                     {'name':'time','type':'LONG'}"
                    + "                 ]," + "     "
                    + "     'payloadData':  ["
                    + "                     {'name':'status','type':'STRING'}"
                    + "                     ]"
                    + "}";

    public final static String MOTION_STREAM_DEFINITION =
            "{"
                    + "     'name':'org_wso2_iot_devices_motion',"
                    + "     'version':'1.0.0',"
                    + "     'nickName': 'MOTION Data',"
                    + "     'description': 'Motion data received from the Device',"
                    + "     'tags': ['iot', 'motion'],"
                    + "     'metaData': ["
                    + "                     {'name':'owner','type':'STRING'},"
                    + "                     {'name':'deviceType','type':'STRING'},"
                    + "                     {'name':'deviceId','type':'STRING'},"
                    + "                     {'name':'time','type':'LONG'}"
                    + "                 ]," + "     "
                    + "     'payloadData':  ["
                    + "                     {'name':'motion','type':'STRING'}"
                    + "                     ]"
                    + "}";

    public final static String SONAR_STREAM_DEFINITION =
            "{"
                    + "     'name':'org_wso2_iot_devices_sonar',"
                    + "     'version':'1.0.0',"
                    + "     'nickName': 'Sonar Data',"
                    + "     'description': 'Sonar data received from the Device',"
                    + "     'tags': ['iot', 'sonar'],"
                    + "     'metaData': ["
                    + "                     {'name':'owner','type':'STRING'},"
                    + "                     {'name':'deviceType','type':'STRING'},"
                    + "                     {'name':'deviceId','type':'STRING'},"
                    + "                     {'name':'time','type':'LONG'}"
                    + "                 ]," + "     "
                    + "     'payloadData':  ["
                    + "                     {'name':'sonar','type':'STRING'}"
                    + "                     ]"
                    + "}";


    public final static String LIGHT_STREAM_DEFINITION =
            "{"
                    + "     'name':'org_wso2_iot_devices_light',"
                    + "     'version':'1.0.0',"
                    + "     'nickName': 'Light Data',"
                    + "     'description': 'Light data received from the Device',"
                    + "     'tags': ['iot', 'light'],"
                    + "     'metaData': ["
                    + "                     {'name':'owner','type':'STRING'},"
                    + "                     {'name':'deviceType','type':'STRING'},"
                    + "                     {'name':'deviceId','type':'STRING'},"
                    + "                     {'name':'time','type':'LONG'}"
                    + "                 ]," + "     "
                    + "     'payloadData':  ["
                    + "                     {'name':'light','type':'STRING'}"
                    + "                     ]"
                    + "}";

}
