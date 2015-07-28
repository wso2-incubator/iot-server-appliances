/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.devicemgt.raspberry.agent;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.log4j.Logger;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.concurrent.FutureCallback;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;


public class SidhdhiQuery implements Runnable {
    private static final Logger log = Logger.getLogger(SidhdhiQuery.class);
    final AgentConstants constants = new AgentConstants();

    boolean isBulbOn = false;


    public void run() {
        // Creating Siddhi Manager
        SiddhiManager siddhiManager = new SiddhiManager();


        while (true) {

            String executionPlan = null;

            executionPlan = readFile(constants.prop.getProperty("execution.plan.file.location"), StandardCharsets.UTF_8);

            if(executionPlan==null) {
                executionPlan = readSonarData(constants.prop.getProperty("sonar.reading.url"));
            }
            //Generating runtime
            ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(executionPlan);

            executionPlanRuntime.addCallback("query1", new QueryCallback() {
                @Override
                public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                    if(inEvents.length > 0) {
                        if(!isBulbOn){
                            performHTTPCall(timeStamp, inEvents, removeEvents, "bulb.on.api.endpoint", "Bulb Switched on!");
                            isBulbOn = true;
                        }
                    }
                }

            });

            //Adding callback to retrieve output events from query
            executionPlanRuntime.addCallback("query1", new QueryCallback() {
                @Override
                public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                    EventPrinter.print(timeStamp, inEvents, removeEvents);
                }
            });


            executionPlanRuntime.addCallback("query2", new QueryCallback() {
                @Override
                public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                    if(isBulbOn){
                        performHTTPCall(timeStamp, inEvents, removeEvents, "bulb.off.api.endpoint","Bulb Switched off!");
                        isBulbOn = false;
                    }
                }

            });

            //Retrieving InputHandler to push events into Siddhi
            InputHandler inputHandler = executionPlanRuntime.getInputHandler("fireAlarmEventStream");

            //Starting event processing
            executionPlanRuntime.start();
            System.out.println("Execution Plan Started!");

            //Sending events to Siddhi
            try {
                String sonarReading = readFile(constants.prop.getProperty("sonar.reading.file.path"), StandardCharsets.UTF_8);
                inputHandler.send(new Object[]{"FIRE_1", 30.0, Double.parseDouble(sonarReading), 20.00});
                Thread.sleep(Integer.parseInt(constants.prop.getProperty("read.interval")));
                executionPlanRuntime.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void performHTTPCall(long timeStamp, Event[] inEvents, Event[] removeEvents, String bulbEP, String logText) {
        if (inEvents != null && inEvents.length > 0) {
            EventPrinter.print(timeStamp, inEvents, removeEvents);
            String url = constants.prop.getProperty(bulbEP);

            CloseableHttpAsyncClient httpclient = null;
            try {

                httpclient = HttpAsyncClients.createDefault();
                httpclient.start();
                HttpGet request = new HttpGet(url);
                System.out.println("Bulb Status : "+logText);
                final CountDownLatch latch = new CountDownLatch(1);
                Future<HttpResponse> future = httpclient.execute(
                        request, new FutureCallback<HttpResponse>() {
                            @Override
                            public void completed(HttpResponse httpResponse) {
                                latch.countDown();
                            }

                            @Override
                            public void failed(Exception e) {
                                latch.countDown();
                            }

                            @Override
                            public void cancelled() {
                                latch.countDown();
                            }
                        });

                latch.await();

            } catch (InterruptedException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Sync Interrupted");
                }
            }
        }
    }

    static String readFile(String path, Charset encoding){
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            log.error("Error reading Sidhdhi query from file.");
        }
        return new String(encoded, encoding);
    }

    private String readSonarData(String sonarAPIUrl){
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(sonarAPIUrl);
        String responseStr = null;
        try {
            HttpResponse response = client.execute(request);
            System.out.print("Response Code : " + response);
            responseStr = response.toString();

        } catch (IOException e) {
            //log.error("Exception encountered while trying to make get request.");
            System.out.print("ERROR!!!");
            return responseStr;
        }
        return responseStr;
    }


}
