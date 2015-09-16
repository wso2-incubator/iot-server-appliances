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

package org.wso2.carbon.device.mgt.iot.agent.kura.display.resource;

import org.apache.commons.configuration.ConfigurationException;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.util.Browser;

import java.util.Map;
import java.util.logging.Logger;

public class UrlResource extends ResourceType{

	private final static Logger log = Logger.getLogger(UrlResource.class.getName());

	@Override
	public void run(Map<String,Object> args) {
		log.info("run()");
		Browser browser = (Browser)args.get("browser");
		try {
			browser.open(getNavigationUrl());
		} catch (ConfigurationException e) {
			log.severe("Resource Run Error: " + e.getMessage());
		}
	}

	@Override
	public void stop(Map<String,Object> args) {
		log.info("stop()");
	}

	private String getNavigationUrl() throws ConfigurationException {
		String url;
		if(configArgs.get("url") != null){
			url = configArgs.get("url");
		}else if(configArgs.get("app") != null){
			url = "http://localhost:8000/" + configArgs.get("app");
		}else{
			throw new ConfigurationException("No valid `url` or `app` attribute found on the Resource");
		}
		return url;
	}
}
