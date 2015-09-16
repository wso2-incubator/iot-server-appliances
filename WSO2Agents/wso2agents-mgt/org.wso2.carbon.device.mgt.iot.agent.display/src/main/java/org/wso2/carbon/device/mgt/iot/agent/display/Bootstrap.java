package org.wso2.carbon.device.mgt.iot.agent.kura.display;

import org.wso2.carbon.device.mgt.iot.agent.kura.display.config.ConfigConstants;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.config.ConfigManager;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.resource.ResourceUtil;

import java.io.File;
import java.util.logging.Logger;

public class Bootstrap {

	private final static Logger log = Logger.getLogger(Bootstrap.class.getName());

	/**
	 * Displays the application details.
	 */
	private void displayBanner() {
		ConfigManager configManager = ConfigManager.getInstance();

		String appName = configManager.getRunnerConfig().getString(
				ConfigConstants.RUNNER_CONFIG_NAME);

		String appKey = configManager.getRunnerConfig().getString(
				ConfigConstants.RUNNER_CONFIG_SERVER_KEY);

		String appVersion = configManager.getRunnerConfig().getString(
				ConfigConstants.RUNNER_CONFIG_VERSION);

		log.info(appName + "(" + appKey + ") " + appVersion + "\n");
	}

	/**
	 * Starts HTTP Server.
	 */
	private void startHttpServer() {
		//get doc root
		ConfigManager configManager = ConfigManager.getInstance();
		String displayAgentHome = ResourceUtil.getDisplayAgentHome();
		File serverDocRoot = new File(
				displayAgentHome + File.separator + LauncherConstants.WEB_APPS_PATH);

		//get server port
		int serverPort = configManager.getRunnerConfig().getInt(
				ConfigConstants.RUNNER_CONFIG_KEY_WEB_BROWSER_PORT,
				LauncherConstants.DEFAULT_SERVER_PORT);

		//start server
		HttpServer httpServer = new HttpServer(serverDocRoot, serverPort);
		Thread httpServerThread = new Thread(httpServer);
		httpServerThread.start();

	}

	/**
	 * Starts Sequence Runnner.
	 */
	private void startSequenceRunner() {
		SequenceRunner sequenceRunner = new SequenceRunner();
		Thread sequenceRunnerThread = new Thread(sequenceRunner);
		sequenceRunnerThread.start();
	}

	/**
	 * Starts Update Manager.
	 */
	private void startUpdateManager() {
		UpdateManager updateManager = UpdateManager.getInstance();
		updateManager.startScript();
	}

	/**
	 * Initiates Config Manager.
	 */
	private void initConfigManager() {
		ConfigManager configManager = ConfigManager.getInstance();
		configManager.initConfig();
	}

	public static void main(String[] args) {
		Bootstrap displayAgent = new Bootstrap();
		displayAgent.initConfigManager();
		displayAgent.displayBanner();
		displayAgent.startHttpServer();
		displayAgent.startSequenceRunner();
		displayAgent.startUpdateManager();
	}

}