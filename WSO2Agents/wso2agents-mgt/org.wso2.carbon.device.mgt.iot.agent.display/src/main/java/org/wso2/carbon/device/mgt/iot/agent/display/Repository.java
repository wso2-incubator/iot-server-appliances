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

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

public class Repository {

	private final static Logger log = Logger.getLogger(Repository.class.getName());

	private String repoName;
	private Map<String, String> commandsList;
	private String tempFolder;

	public Repository(String repoName, Map<String, String> commandsList, String tempFolderPath) {
		this.repoName = repoName;
		this.commandsList = commandsList;
		this.tempFolder = tempFolderPath;
	}

	public boolean init() {
		File currentDir = new File(tempFolder);

		String cmd = commandsList.get("Init");
		try {
			executeCommand(currentDir, "/bin/bash", "-c", cmd);
			return true;
		} catch (RepositoryException e) {
			return false;
		}
	}

	public boolean checkUpdates() {
		File currentDir = new File(tempFolder + File.separator + repoName);
		ProcessResult localRevResult = null;
		ProcessResult remoteRevResult = null;

		try {
			String cmd = commandsList.get("LocalRevision");
			localRevResult = executeCommand(currentDir, "/bin/bash", "-c", cmd);

			cmd = commandsList.get("RemoteRevision");
			remoteRevResult = executeCommand(currentDir, "/bin/bash", "-c", cmd);
		}catch(RepositoryException e){
			return false;
		}

		return (localRevResult.getOutput().equals(remoteRevResult.getOutput()));
	}

	public boolean updateRepo() {
		File currentDir = new File(tempFolder + File.separator + repoName);

		String cmd = commandsList.get("Update");
		try {
			ProcessResult updateResult = executeCommand(currentDir, "/bin/bash", "-c", cmd);
			return true;
		} catch (RepositoryException e) {
			return false;
		}
	}

	private ProcessResult executeCommand(File currentDir, String... command) throws RepositoryException {
		ProcessBuilder builder = new ProcessBuilder(command);

		log.info("Executable dir: " + currentDir.getAbsoluteFile());
		builder.directory(currentDir.getAbsoluteFile()); //setting executable's current dir

		builder.redirectErrorStream(true);
		Process process = null;
		try {
			process = builder.start();
		} catch (IOException e) {
			String msg = "Error on starting process: " + e.getMessage();
			log.severe(msg);
			throw new RepositoryException(msg);
		}

		Scanner s = new Scanner(process.getInputStream());
		StringBuilder text = new StringBuilder();
		while (s.hasNextLine()) {
			text.append(s.nextLine());
			text.append("\n");
		}
		s.close();

		int result = 0;
		try {
			result = process.waitFor();
		} catch (InterruptedException e) {
			String msg = "Error on waiting process: " + e.getMessage();
			log.severe(msg);
			throw new RepositoryException(msg);
		}

		log.info("Process exited with result " + result + " and output " + text);
		ProcessResult processResult = new ProcessResult(result, text.toString());
		return processResult;
	}

	private class RepositoryException extends Exception {

		public RepositoryException() {
		}

		public RepositoryException(String message) {
			super(message);
		}

		public RepositoryException(Throwable cause) {
			super(cause);
		}

		public RepositoryException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	private class ProcessResult{
		private int result;
		private String output;

		public ProcessResult(int result, String output) {
			this.result = result;
			this.output = output;
		}

		public int getResult() {
			return result;
		}

		public String getOutput() {
			return output;
		}

	}

}
