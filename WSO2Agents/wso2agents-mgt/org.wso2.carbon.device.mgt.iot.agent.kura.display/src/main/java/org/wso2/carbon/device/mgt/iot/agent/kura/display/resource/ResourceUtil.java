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

import org.osgi.framework.BundleException;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.internal.DisplayAgentServiceComponent;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.LauncherConstants;

import java.io.File;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceUtil {

	private final static Logger log = Logger.getLogger(ResourceUtil.class.getName());


	public static long getMillis(String timeString){
		if(timeString == null || timeString.isEmpty()){return 0L;}

		long millis = 0L;
		Pattern pattern = Pattern.compile("(\\d+)([dhms])");
		Matcher matcher = pattern.matcher(timeString);

		while (matcher.find()) {
			if (matcher.groupCount() >= 2) {

				int time = Integer.parseInt(matcher.group(1));
				String timeStr = matcher.group(2);

				if (timeStr.equals("d")) {
					millis += time * 24 * 60 * 60 * 1000;
				} else if (timeStr.equals("h")) {
					millis += time * 60 * 60 * 1000;
				} else if (timeStr.equals("m")) {
					millis += time * 60 * 1000;
				} else if (timeStr.equals("s")) {
					millis += time * 1000;
				}

			}
		}
		return millis;
	}

	public static boolean createFolder(String path){
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
			return true;
		}
		return false;
	}

	public static String getDisplayAgentHome(){
		//try system property defined as -Ddisplay.agent.home=""
		String displayAgentHome = System.getProperty(LauncherConstants.DISPLAY_AGENT_HOME);
		if(displayAgentHome != null){
			log.info("===== VM parameter found! =====");
			return displayAgentHome;
		}

		//return default
		displayAgentHome = System.getenv("DISPLAY_AGENT_HOME");
		if(displayAgentHome != null){
			log.info("===== ENV param found! =====");
			return displayAgentHome;
		}

		//String currentUsersHomeDir = System.getProperty("user.home"); <-- this returns root
		String currentUsersHomeDir = "/home/pi";
		displayAgentHome = currentUsersHomeDir + File.separator + "DigitalDisplay";
		log.info("===== default found! =====");
		log.info("Couldn't find system property `display.agent.home` using default: `" + displayAgentHome + "`!");
		return displayAgentHome;
	}

	public static boolean stopBundle(){
		try {
			DisplayAgentServiceComponent.getBundleContext().getBundle().stop();
			return true;
		} catch (BundleException e) {
			//do nothing
			return false;
		}
	}

}
