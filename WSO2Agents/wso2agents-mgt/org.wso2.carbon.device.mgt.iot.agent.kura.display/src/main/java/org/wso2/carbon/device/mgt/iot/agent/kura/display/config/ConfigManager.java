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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.resource.ResourceUtil;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.LauncherConstants;

import java.io.File;
import java.util.logging.Logger;

public class ConfigManager {

	private final static Logger log = Logger.getLogger(ConfigManager.class.getName());

	private static final ConfigManager instance = new ConfigManager();
	private HierarchicalConfiguration runner_config = null;
	private HierarchicalConfiguration content_config = null;

	private ConfigManager() {
	}

	public static ConfigManager getInstance() {
		return ConfigManager.instance;
	}

	/**
	 * Initialize the configurations.
	 */
	public void initConfig() {
		String displayAgentHome = ResourceUtil.getDisplayAgentHome();
		runner_config = getConfiguration(displayAgentHome + File.separator +
												 LauncherConstants.CONF_PATH + File.separator
												 + "digital_display_runner.xml");
		content_config = getConfiguration(displayAgentHome + File.separator +
												  LauncherConstants.CONF_PATH + File.separator
												  + "digital_display_content.xml");
	}

	public HierarchicalConfiguration getRunnerConfig() {
		return runner_config;
	}

	public HierarchicalConfiguration getContentConfig() {
		return content_config;
	}

	/**
	 * Returns configuration for xml file.
	 * @param xmlConfigFile path to xml file
	 * @return
	 */
	public HierarchicalConfiguration getConfiguration(String xmlConfigFile) {
		HierarchicalConfiguration config = null;
		try {
			File file = new File(xmlConfigFile);
			config = loadConfigFile(file);
		} catch (ConfigurationException e) {
			log.severe("Couldn't load the configuration file `" + xmlConfigFile + "`");
			ResourceUtil.stopBundle();
		}
		return config;
	}

	private HierarchicalConfiguration loadConfigFile(File configXmlFileName)
			throws ConfigurationException {
		XMLConfiguration config = null;
		config = new XMLConfiguration(configXmlFileName);
		FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
		strategy.setRefreshDelay(config.getInt(ConfigConstants.CONFIG_REFRESH_DELAY, 5000));
		config.setReloadingStrategy(strategy);

		return config;
	}
}
