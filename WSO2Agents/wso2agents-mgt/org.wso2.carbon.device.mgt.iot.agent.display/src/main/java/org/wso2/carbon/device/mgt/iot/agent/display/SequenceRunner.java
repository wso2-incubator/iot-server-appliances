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

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.config.ConfigConstants;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.config.ConfigManager;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.resource.ResourceType;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.util.Browser;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.util.DisplayServiceContext;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.util.Player;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.util.impl.AJAXService;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.util.impl.GenericBrowser;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.util.impl.OMXPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SequenceRunner implements Runnable{

	private final static Logger log = Logger.getLogger(SequenceRunner.class.getName());

	public void run(){
		List<ResourceType> sequence = getSequence();


		ConfigManager configManager = ConfigManager.getInstance();
		String browserPath = configManager.getRunnerConfig().getString(
				ConfigConstants.RUNNER_CONFIG_KEY_WEB_BROWSER_PATH);

		Browser browser = new GenericBrowser(browserPath);
		browser.open("http://localhost:8000/_system");

		Player player = new OMXPlayer();
		DisplayServiceContext displayServiceContext = new AJAXService();

		int seq_index = 0;
		while (seq_index < sequence.size()) {
			ResourceType currentResource = sequence.get(seq_index);

			Map<String,Object> args= new HashMap<>();
			args.put("browser", browser);
			args.put("player", player);
			args.put("displayServiceContext", displayServiceContext);

			currentResource.run(args);

			try {
				long waitingTime = currentResource.getOnScreenDelay() + currentResource.getPreScreenDelay();
				log.info("==========SequenceRunnerThread sleep started! ->" + waitingTime);
				Thread.sleep(waitingTime);
				log.info("==========SequenceRunnerThread sleep stopped!");
			} catch (InterruptedException e) {
				log.severe("==========SequenceRunnerThread sleep interrupted!");
			}

			currentResource.stop(args);

			seq_index++;
			if(seq_index == sequence.size()){
				seq_index = 0;
			}
		}
	}

	private List<ResourceType> getSequence() {
		List<ResourceType> sequence = new ArrayList<>();

		ConfigManager configManager = ConfigManager.getInstance();

		List<HierarchicalConfiguration> resources = configManager.getContentConfig()
				.configurationsAt("Content.DisplaySequence.Resource");

		for (HierarchicalConfiguration resource : resources) {

			String resourceTypeHandler = resource.getString("[@handler]");
			ResourceType resourceObj = null;

			try {
				resourceObj = (ResourceType) Class.forName(resourceTypeHandler).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				log.severe("Error occurred while resolving resource type: " + e.getMessage());
			}

			//read all init args
			Map<String,String> configArgs = new HashMap<>();
			ConfigurationNode node = resource.getRootNode();
			List<ConfigurationNode> attrs = node.getAttributes();

			for (ConfigurationNode attr : attrs) {
				configArgs.put(attr.getName(), attr.getValue().toString());
			}

			//initialize the resource with config args
			resourceObj.init(configArgs);

			//add it to the sequence
			sequence.add(resourceObj);
		}

		return sequence;
	}

}
