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

package org.wso2.carbon.device.mgt.iot.agent.kura.display.util.impl;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.LauncherConstants;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.resource.ResourceUtil;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.util.DisplayServiceContext;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class AJAXService implements DisplayServiceContext {

	private final static Logger log = Logger.getLogger(AJAXService.class.getName());

	@Override
	public boolean setCurrentIFrameUrl(String url, long frameLoadDelay, long nextPollTime) {
		synchronized (AJAXService.class) {
			if (url == null || url.isEmpty()) {
				return false;
			}

			JSONObject jsonObject = readResourceJson();
			jsonObject.put("path", url);
			jsonObject.put("next_poll", nextPollTime);
			jsonObject.put("load_delay", frameLoadDelay);
			writeResourceJson(jsonObject);
			return true;
		}
	}

	private JSONObject readResourceJson() {
		JSONParser parser = new JSONParser();
		String displayAgentHome = ResourceUtil.getDisplayAgentHome();
		Object obj = null;

		try {
			obj = parser.parse(new FileReader(
					displayAgentHome + File.separator + LauncherConstants.WEB_APPS_PATH + File.separator
							+ "_system" + File.separator + "current_resource.json"));
		} catch (IOException e) {
			log.severe("Error occurred while reading from `current_resource.json` ! \n" + e
					.getMessage());
		} catch (ParseException e) {
			log.severe("Parse error occurred while reading `current_resource.json` ! \n" + e
					.getMessage());
		}

		return (JSONObject) obj;
	}

	private void writeResourceJson(JSONObject jsonObject) {
		try {
			String displayAgentHome = ResourceUtil.getDisplayAgentHome();
			FileWriter file = new FileWriter(
					displayAgentHome + File.separator + LauncherConstants.WEB_APPS_PATH + File.separator
							+ "_system" + File.separator + "current_resource.json");
			file.write(jsonObject.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			log.severe("Error occurred while writing to `current_resource.json` ! \n" + e
					.getMessage());
		}
	}

}
