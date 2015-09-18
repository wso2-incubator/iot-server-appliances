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

package org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.impl.internal;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.AgentOperation;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.impl.AgentOperationVirtualImpl;

/**
 * @scr.component name="org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.impl" immediate="true"
 */

public class AgentServiceVirtualComponent {
    private static final Logger log = LoggerFactory.getLogger(AgentServiceVirtualComponent.class);

    protected void activate(ComponentContext componentContext) {
        log.info("===================");
        log.info("Virtual Agent Bundle has started!");
        log.info("===================");
        /* Registering DeviceGroup Management service */
        BundleContext bundleContext = componentContext.getBundleContext();
        AgentOperation agentOperation = new AgentOperationVirtualImpl();
        bundleContext.registerService(AgentOperation.class.getName(), agentOperation, null);
        if (log.isDebugEnabled()) {
            log.debug("Agent virtual emulation bundle has been successfully initialized");
        }
    }

    protected void deactivate(ComponentContext componentContext) {
        log.info("===================");
        log.info("Virtual Agent Bundle has stopped!");
        log.info("===================");
    }
}
