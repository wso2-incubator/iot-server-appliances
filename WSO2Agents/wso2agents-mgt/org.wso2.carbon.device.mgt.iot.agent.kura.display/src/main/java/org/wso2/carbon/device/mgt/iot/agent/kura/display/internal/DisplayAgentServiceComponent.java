/*
 *
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
 *
 *
 */

package org.wso2.carbon.device.mgt.iot.agent.kura.display.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import java.util.logging.Logger;

import org.wso2.carbon.device.mgt.iot.agent.kura.display.Bootstrap;

/**
 * @scr.component name="DisplayAgentServiceComponent"
 * immediate="true"
 */
public class DisplayAgentServiceComponent implements BundleActivator {
	private final static Logger log = Logger.getLogger(DisplayAgentServiceComponent.class.getName());

	private static final String APP_ID = "display agent";
	private static BundleContext bundleContext;

	public static BundleContext getBundleContext() {
		return bundleContext;
	}

	public void start(BundleContext bundleContext) throws Exception {
		DisplayAgentServiceComponent.bundleContext = bundleContext;
		log.info("==================================");
		log.info("Bundle " + APP_ID + " has started!");
		log.info("===================================");
		Bootstrap.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		log.info("==================================");
		log.info("Bundle " + APP_ID + " has stopped!");
		log.info("==================================");
		DisplayAgentServiceComponent.bundleContext = null;
	}

}
