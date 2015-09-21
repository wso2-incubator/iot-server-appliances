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

import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.operation.AgentOperationManager;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.operation.SimpleServer;

public class AgentDataHolder {

    private static AgentDataHolder thisInstance = new AgentDataHolder();
    private AgentOperationManager agentOperationManager;
    private SimpleServer simpleServer;

    private AgentDataHolder() {

    }

    public static AgentDataHolder getInstance() {
        return thisInstance;
    }

    public void init() {
        simpleServer = new SimpleServer();
    }

    public AgentOperationManager getAgentOperationManager() {
        return agentOperationManager;
    }

    public void setAgentOperationManager(AgentOperationManager agentOperationManager) {
        this.agentOperationManager = agentOperationManager;
    }

    public SimpleServer getSimpleServer() {
        return simpleServer;
    }
}
