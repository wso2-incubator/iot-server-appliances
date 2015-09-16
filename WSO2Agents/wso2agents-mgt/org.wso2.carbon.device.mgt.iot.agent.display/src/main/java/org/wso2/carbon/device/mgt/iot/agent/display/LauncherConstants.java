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

package org.wso2.carbon.device.mgt.iot.agent.kura.display;

import java.io.File;

public final class LauncherConstants {

	private LauncherConstants() {
	}

	public static final String DISPLAY_AGENT_HOME = "display.agent.home";
	//
	public static final String REPOSITORY_DIR = "repository";
	public static final String CONF_DIR = "conf";
	public static final String DEPLOYMENT_DIR = "deployment";
	public static final String WEB_APPS_DIR = "webapps";
	public static final String SECURITY_DIR = "security";
	public static final String SCRIPTS_DIR = "scripts";
	//
	public static final String CARBON_KEYSTORE = "wso2carbon.jks";
	public static final String CARBON_KEYSTORE_SECRET = "wso2carbon";
	//
	public static final String CONF_PATH = REPOSITORY_DIR + File.separator + CONF_DIR;
	public static final String WEB_APPS_PATH = REPOSITORY_DIR + File.separator + DEPLOYMENT_DIR + File.separator + WEB_APPS_DIR;
	public static final String CARBON_KEYSTORE_PATH = SECURITY_DIR + File.separator + CARBON_KEYSTORE;
	public static final String SCRIPTS_PATH = REPOSITORY_DIR + File.separator + SCRIPTS_DIR;
	//
	public static final int DEFAULT_SERVER_PORT = 8080;
	public static final int DEFAULT_SHOW_UP_WAITING_TIME = 5000;
	public static final int DEFAULT_PRE_SCREENING_WAITING_TIME = 1000;
}