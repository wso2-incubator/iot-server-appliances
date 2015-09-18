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

package org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.internal;

import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.AgentOperation;

/**
 * @scr.component name="org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core" immediate="true"
 * @scr.reference name="org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.impl"
 * interface="org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.AgentOperation"
 * cardinality="1..1"
 * policy="dynamic"
 * bind="setImplService"
 * unbind="unsetImplService"
 */

public class AgentServiceComponent {
    private static final Logger log = LoggerFactory.getLogger(AgentServiceComponent.class);

    protected void activate(ComponentContext componentContext) {
        log.info("===================");
        log.info("Agent Core Bundle has started!");
        log.info("===================");
    }

    protected void deactivate(ComponentContext componentContext) {
        log.info("===================");
        log.info("Agent Core Bundle has stopped!");
        log.info("===================");
    }

    /**
     * Sets Agent Operations.
     *
     * @param agentOperation An instance of AgentOperation
     */
    protected void setImplService(AgentOperation agentOperation) {
        log.info("===================");
        log.info("Agent Operations Binned!");
        log.info("===================");
        AgentDataHolder.getInstance().setAgentOperation(agentOperation);
    }

    /**
     * Un sets Agent Operations.
     *
     * @param agentOperation An instance of AgentOperation
     */
    protected void unsetImplService(AgentOperation agentOperation) {
        log.info("===================");
        log.info("Agent Operations Un-binned!");
        log.info("===================");
        AgentDataHolder.getInstance().setAgentOperation(null);
    }
}
