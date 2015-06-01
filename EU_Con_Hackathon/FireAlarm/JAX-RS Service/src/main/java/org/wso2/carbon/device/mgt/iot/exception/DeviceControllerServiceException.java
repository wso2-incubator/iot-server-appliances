package org.wso2.carbon.device.mgt.iot.exception;

/**
 * Created by smean-MAC on 5/29/15.
 */
public class DeviceControllerServiceException extends Exception{

    public DeviceControllerServiceException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DeviceControllerServiceException(Throwable cause) {
        super(cause);
    }

    public DeviceControllerServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeviceControllerServiceException(String message) {
        super(message);
    }

    public DeviceControllerServiceException() {
    }
}
