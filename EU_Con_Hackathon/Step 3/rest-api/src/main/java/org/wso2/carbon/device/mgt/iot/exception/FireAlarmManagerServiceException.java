package org.wso2.carbon.device.mgt.iot.exception;

/**
 * Created by smean-MAC on 5/29/15.
 */
public class FireAlarmManagerServiceException extends Exception {

    public FireAlarmManagerServiceException() {
    }

    public FireAlarmManagerServiceException(String msg) {

    }

    public FireAlarmManagerServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FireAlarmManagerServiceException(Throwable cause) {
        super(cause);
    }

    public FireAlarmManagerServiceException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
