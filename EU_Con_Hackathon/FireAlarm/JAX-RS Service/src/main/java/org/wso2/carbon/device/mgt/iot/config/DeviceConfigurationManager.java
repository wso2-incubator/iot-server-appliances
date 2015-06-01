/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.iot.config;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.iot.exception.DeviceControllerServiceException;
import org.wso2.carbon.device.mgt.iot.util.IotDeviceManagementUtil;
import org.wso2.carbon.utils.CarbonUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Class responsible for the iot device manager configuration initialization.
 */
public class DeviceConfigurationManager {

    Logger log = Logger.getLogger(DeviceConfigurationManager.class);

    private static final String IOT_DEVICE_CONFIG_XML_NAME = "devicecloud-config.xml";
	private static final String IOT_DC_ROOT_DIRECTORY = "iot";
	private final String CONFIGS_FILE_LOCATION =
			CarbonUtils.getCarbonConfigDirPath() + File.separator +
                    IOT_DC_ROOT_DIRECTORY + File.separator + IOT_DEVICE_CONFIG_XML_NAME;

    private DeviceManagementConfig currentFireAlarmMgtConfig;
    private static DeviceConfigurationManager deviceConfigurationManager;

    private DeviceConfigurationManager() {
    }

    public static DeviceConfigurationManager getInstance() throws DeviceControllerServiceException {
        if (deviceConfigurationManager == null) {
            synchronized (DeviceConfigurationManager.class) {
                if (deviceConfigurationManager == null) {
                    DeviceConfigurationManager result = new DeviceConfigurationManager();
                    result.initConfig();
                    deviceConfigurationManager = result;
                }
            }

        }
        return deviceConfigurationManager;
    }

    private void initConfig() throws DeviceControllerServiceException {
        try {
            File fireAlarmMgtConfig = new File(CONFIGS_FILE_LOCATION);
            Document doc = IotDeviceManagementUtil.convertToDocument(fireAlarmMgtConfig);
            JAXBContext fireAlarmMgtContext = JAXBContext.newInstance(DeviceManagementConfig.class);
            Unmarshaller unmarshaller = fireAlarmMgtContext.createUnmarshaller();
            this.currentFireAlarmMgtConfig = (DeviceManagementConfig) unmarshaller.unmarshal(doc);
        } catch (Exception e) {
            String error = "Error occurred while initializing DeviceController configurations";
            log.error(error);
            throw new DeviceControllerServiceException(error, e);
        }
    }

    public DeviceManagementConfig getFireAlarmMgtConfig() {
        return currentFireAlarmMgtConfig;
    }

}
