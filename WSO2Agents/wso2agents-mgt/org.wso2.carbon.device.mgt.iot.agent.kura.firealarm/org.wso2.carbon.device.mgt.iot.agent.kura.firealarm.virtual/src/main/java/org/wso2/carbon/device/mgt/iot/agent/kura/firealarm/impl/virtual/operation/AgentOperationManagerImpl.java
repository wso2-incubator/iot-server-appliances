/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.impl.virtual.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.constants.AgentConstants;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.operation.AgentOperationManager;

public class AgentOperationManagerImpl implements AgentOperationManager {

    private static final Logger log = LoggerFactory.getLogger(AgentOperationManagerImpl.class);

    public void changeBulbStatus(boolean status) {
        log.info("Bulb status: " + (status ? AgentConstants.CONTROL_ON : AgentConstants.CONTROL_OFF));
    }

    public double getTemperature() {
        double temp = Math.random() * 100;
        log.info("Temperature: " + temp);
        return temp;
    }

    public double getHumidity() {
        double hum = Math.random() * (80 - 10) + 10;
        log.info("Humidity: " + hum);
        return hum;
    }
}
