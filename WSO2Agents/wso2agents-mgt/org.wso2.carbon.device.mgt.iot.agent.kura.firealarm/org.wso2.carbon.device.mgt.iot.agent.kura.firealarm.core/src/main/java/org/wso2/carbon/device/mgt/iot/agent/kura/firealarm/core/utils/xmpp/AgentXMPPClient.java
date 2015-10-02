/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.utils.xmpp;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.constants.AgentConstants;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.exception
		.AgentCoreOperationException;

public abstract class AgentXMPPClient {
	private static final Logger log = LoggerFactory.getLogger(AgentXMPPClient.class);

	private int replyTimeoutInterval = 500;    // millis
	private String server;
	private int port;

	private ConnectionConfiguration config;
	private XMPPConnection connection;

	private PacketFilter filter;
	private PacketListener listener;

	public AgentXMPPClient(String server, int port) {
		this.server = server;
		this.port = port;
		initXMPPClient();
	}

	private void initXMPPClient() {
		log.info(AgentConstants.LOG_APPENDER + String.format(
				"Initializing connection to XMPP Server at %1$s via port %2$d......", server,
				port));
		SmackConfiguration.setPacketReplyTimeout(replyTimeoutInterval);
		config = new ConnectionConfiguration(server, port);
//		TODO:: Need to enable SASL-Authentication appropriately
		config.setSASLAuthenticationEnabled(false);
		config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
		connection = new XMPPConnection(config);
	}

	public void connectAndLogin(String username, String password, String resource)
			throws AgentCoreOperationException {
		try {
			connection.connect();
			log.info(AgentConstants.LOG_APPENDER + String.format(
					"Connection to XMPP Server at %1$s established successfully......", server));

		} catch (XMPPException xmppExcepion) {
			String errorMsg =
					"Connection attempt to the XMPP Server at " + server + " via port " + port +
							" failed.";
			log.info(AgentConstants.LOG_APPENDER + errorMsg);
			throw new AgentCoreOperationException(errorMsg, xmppExcepion);
		}

		if (connection.isConnected()) {
			try {
				if (resource == null) {
					connection.login(username, password);
					log.info(AgentConstants.LOG_APPENDER + String.format(
							"Logged into XMPP Server at %1$s as user %2$s......", server,
							username));
				} else {
					connection.login(username, password, resource);
					log.info(AgentConstants.LOG_APPENDER + String.format(
							"Logged into XMPP Server at %1$s as user %2$s on resource %3$s......",
							server, username, resource));
				}
			} catch (XMPPException xmppExcepion) {
				String errorMsg =
						"Login attempt to the XMPP Server at " + server + " with username - " +
								username + " failed.";
				log.info(AgentConstants.LOG_APPENDER + errorMsg);
				throw new AgentCoreOperationException(errorMsg, xmppExcepion);
			}
		}
	}

	public void setFilterOnSender(String senderJID) {
		filter = new AndFilter(new PacketTypeFilter(Message.class), new FromContainsFilter(
				senderJID));
		listener = new PacketListener() {
			@Override
			public void processPacket(Packet packet) {
				if (packet instanceof Message) {
					final Message xmppMessage = (Message) packet;
					Thread msgProcessThread = new Thread() {
						public void run() {
							processXMPPMessage(xmppMessage);
						}
					};
					msgProcessThread.start();
				}
			}
		};

		connection.addPacketListener(listener, filter);
	}

	public void sendXMPPMessage(String JID, String message) {
		sendXMPPMessage(JID, message, "Reply-From-Device");
		if (log.isDebugEnabled()) {
			log.debug(AgentConstants.LOG_APPENDER + "Message: " + message + " to XMPP JID [" +
					          JID +
					          "] sent successfully");
		}
	}

	public void sendXMPPMessage(String JID, String message, String subject) {
		Message xmppMessage = new Message();
		xmppMessage.setTo(JID);
		xmppMessage.setSubject(subject);
		xmppMessage.setBody(message);
		xmppMessage.setType(Message.Type.chat);
		connection.sendPacket(xmppMessage);
	}

	public void setReplyTimeoutInterval(int millis) {
		this.replyTimeoutInterval = millis;
	}

	public void disableDebugger() {
		connection.DEBUG_ENABLED = false;
	}

	public void closeConnection() {
		if (connection != null && connection.isConnected()) {
			connection.disconnect();
		}
	}

	protected abstract void processXMPPMessage(Message xmppMessage);
}

