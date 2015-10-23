package org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.main;

import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.internal.AgentManager;

public class Bootstrap {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
//	    System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "debug");
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
		System.setProperty("org.apache.commons.logging.simplelog.dateTimeFormat", "HH:mm:ss");
		AgentManager agentManager = AgentManager.getInstance();
		agentManager.init();
	}

}
