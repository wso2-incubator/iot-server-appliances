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

import org.wso2.carbon.device.mgt.iot.agent.kura.display.resource.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class UpdateManager {

	private final static Logger log = Logger.getLogger(UpdateManager.class.getName());

	private static final UpdateManager instance = new UpdateManager();

	private UpdateManager(){
	}

	public static UpdateManager getInstance(){
		return UpdateManager.instance;
	}


	public void startScript() {
		synchronized (UpdateManager.class) {
			String displayAgentHome = ResourceUtil.getDisplayAgentHome();
			File currentDir = new File(displayAgentHome + File.separator + LauncherConstants.SCRIPTS_PATH);
			try {
				Process process = executeCommand(currentDir, "/bin/bash", "-c", "./content_update.sh");
			} catch (UpdateManagerException e) {
				log.severe("Update manager failed to update!");
			}
		}
	}

	private Process executeCommand(File currentDir, String... command) throws UpdateManagerException{
		ProcessBuilder builder = new ProcessBuilder(command);

		log.info("Executable dir: " + currentDir.getAbsoluteFile());
		builder.directory(currentDir.getAbsoluteFile()); //setting executable's current dir

		builder.redirectErrorStream(true);

		Process process = null;
		try {
			process = builder.start();
			return process;
		} catch (IOException e) {
			String msg = "Error on starting process: " + e.getMessage();
			log.severe(msg);
			throw new UpdateManagerException(msg);
		}

	}

	private class UpdateManagerException extends Exception {

		public UpdateManagerException() {
		}

		public UpdateManagerException(String message) {
			super(message);
		}

		public UpdateManagerException(Throwable cause) {
			super(cause);
		}

		public UpdateManagerException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	private class ProcessResult{
		private int returnCode;
		private String output;

		public ProcessResult(int returnCode, String output) {
			this.returnCode = returnCode;
			this.output = output;
		}

		public int getReturnCode() {
			return returnCode;
		}

		public String getOutput() {
			return output;
		}

	}
}
