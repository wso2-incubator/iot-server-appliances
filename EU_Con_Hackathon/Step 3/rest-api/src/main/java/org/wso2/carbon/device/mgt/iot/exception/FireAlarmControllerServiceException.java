package org.wso2.carbon.device.mgt.iot.exception;

/**
 * Created by smean-MAC on 5/29/15.
 */
public class FireAlarmControllerServiceException extends Exception {

    public FireAlarmControllerServiceException() {
    }

    public FireAlarmControllerServiceException(String message) {
        super(message);
    }

    public FireAlarmControllerServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FireAlarmControllerServiceException(Throwable cause) {
        super(cause);
    }

    public FireAlarmControllerServiceException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
