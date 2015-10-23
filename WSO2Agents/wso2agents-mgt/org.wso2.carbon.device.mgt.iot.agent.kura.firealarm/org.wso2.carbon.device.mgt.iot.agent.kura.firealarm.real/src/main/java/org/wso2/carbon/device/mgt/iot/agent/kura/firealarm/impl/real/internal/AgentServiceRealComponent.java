package org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.impl.real.internal;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.operation.AgentOperationManager;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.impl.real.operation
		.AgentOperationManagerImpl;
/**
 * @scr.component name="org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.impl" immediate="true"
 */

public class AgentServiceRealComponent {
	private static final Logger log = LoggerFactory.getLogger(AgentServiceRealComponent.class);

	private AgentOperationManager agentOperationManager;

	protected void activate(ComponentContext componentContext) {
        /* Registering DeviceGroup Management service */
		BundleContext bundleContext = componentContext.getBundleContext();
		agentOperationManager = new AgentOperationManagerImpl();
		((AgentOperationManagerImpl) agentOperationManager).init();

		bundleContext.registerService(AgentOperationManager.class.getName(), agentOperationManager, null);

		if (log.isDebugEnabled()) {
			log.debug("Real Agent bundle has been successfully initialized");
		}

		log.info("===================");
		log.info("Real Agent started!");
		log.info("===================");
	}

	protected void deactivate(ComponentContext componentContext) {
		((AgentOperationManagerImpl) agentOperationManager).terminate();
		log.info("===================");
		log.info("Real Agent Bundle has stopped!");
		log.info("===================");
	}

}
