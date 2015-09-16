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

package org.wso2.carbon.device.mgt.iot.agent.kura.display.config;

public final class ConfigConstants {
	private ConfigConstants(){
	}

	public static final String CONFIG_REFRESH_DELAY = "RefreshDelay";
	public static final String RUNNER_CONFIG_NAME = "Name";
	public static final String RUNNER_CONFIG_SERVER_KEY = "ServerKey";
	public static final String RUNNER_CONFIG_VERSION = "Version";
	//
	public static final String RUNNER_CONFIG_KEY_KERNEL_RUNNER = "KernelRunner";
	public static final String RUNNER_CONFIG_KEY_WEB_BROWSER = "WebBrowser";
	public static final String RUNNER_CONFIG_KEY_WEB_BROWSER_PATH = RUNNER_CONFIG_KEY_KERNEL_RUNNER + "." + RUNNER_CONFIG_KEY_WEB_BROWSER + "." + "Path";
	public static final String RUNNER_CONFIG_KEY_WEB_BROWSER_PORT = RUNNER_CONFIG_KEY_KERNEL_RUNNER + "." + RUNNER_CONFIG_KEY_WEB_BROWSER + "." + "Port";
	//
	public static final String RUNNER_CONFIG_KEY_REPO_INIT = "Init";
	public static final String RUNNER_CONFIG_KEY_REPO_UPDATE = "Update";
	public static final String RUNNER_CONFIG_KEY_REPO_LOCAL_REV = "LocalRevision";
	public static final String RUNNER_CONFIG_KEY_REPO_REMOTE_REV = "RemoteRevision";
	//
	public static final String CONTENT_CONFIG_KEY="";
}
