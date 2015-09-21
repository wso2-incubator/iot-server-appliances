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

package org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.operation;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.internal.AgentDataHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SimpleServer {

    private static final Logger log = LoggerFactory.getLogger(SimpleServer.class);

    public SimpleServer() {
        Server server = new Server(9090);
        try {
            server.setHandler(new AbstractHandler() {
                public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse) throws IOException, ServletException {
                    httpServletResponse.setContentType("text/html;charset=utf-8");
                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                    request.setHandled(true);
                    AgentOperationManager agentOperationManager = AgentDataHolder.getInstance()
                            .getAgentOperationManager();
                    if (request.getPathInfo().equals("/temperature")) {
                        httpServletResponse.getWriter().println(agentOperationManager.getTemperature());
                    } else if (request.getPathInfo().equals("/humidity")) {
                        httpServletResponse.getWriter().println(agentOperationManager.getHumidity());
                    } else if (request.getPathInfo().equals("/bulb")) {
                        if (request.getParameter("status") == null) {
                            httpServletResponse.getWriter().println("Please specify status");
                        } else {
                            boolean status = request.getParameter("status").equals("on");
                            agentOperationManager.changeBulbStatus(status);
                            httpServletResponse.getWriter().println("Bulb " + (status ? "ON" : "OFF"));
                        }
                    }
                }
            });
            server.start();
            log.info("Server started");
        } catch (Exception e) {
            log.error("Unable to start server", e);
        }
    }
}
