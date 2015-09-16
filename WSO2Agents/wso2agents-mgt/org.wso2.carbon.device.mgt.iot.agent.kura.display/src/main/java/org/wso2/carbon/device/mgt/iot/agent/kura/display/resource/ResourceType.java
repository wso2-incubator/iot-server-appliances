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

import org.wso2.carbon.device.mgt.iot.agent.kura.display.LauncherConstants;

import java.util.Map;

public abstract class ResourceType {

	protected Map<String, String> configArgs;

	public void init(Map<String, String> configArgs) {
		this.configArgs = configArgs;
	}

	abstract public void run(Map<String, Object> args);

	abstract public void stop(Map<String, Object> args);

	public long getOnScreenDelay() {
		String timeString = this.configArgs.get("time");
		long millis = ResourceUtil.getMillis(timeString);
		return (millis == 0L ? LauncherConstants.DEFAULT_SHOW_UP_WAITING_TIME : millis);
	}

	public long getPreScreenDelay() {
		String timeString = this.configArgs.get("on_screen_delay");
		long millis = ResourceUtil.getMillis(timeString);
		return (millis == 0L ? LauncherConstants.DEFAULT_PRE_SCREENING_WAITING_TIME : millis);
	}

}
