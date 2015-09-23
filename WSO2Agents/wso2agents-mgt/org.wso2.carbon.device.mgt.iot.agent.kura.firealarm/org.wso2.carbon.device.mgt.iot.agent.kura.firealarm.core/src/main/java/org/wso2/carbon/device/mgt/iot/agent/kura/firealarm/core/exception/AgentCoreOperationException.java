package org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.exception;


public class AgentCoreOperationException extends Exception{
	private static final long serialVersionUID = 2736466230451105710L;

	private String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public AgentCoreOperationException(String msg, Exception nestedEx) {
		super(msg, nestedEx);
		setErrorMessage(msg);
	}

	public AgentCoreOperationException(String message, Throwable cause) {
		super(message, cause);
		setErrorMessage(message);
	}

	public AgentCoreOperationException(String msg) {
		super(msg);
		setErrorMessage(msg);
	}

	public AgentCoreOperationException() {
		super();
	}

	public AgentCoreOperationException(Throwable cause) {
		super(cause);
	}
}
