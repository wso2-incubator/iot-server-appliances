package org.wso2.carbon.devicemgt.grant;

import org.apache.amber.oauth2.common.validators.AbstractValidator;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by ace on 7/3/15.
 */
public class DeviceGrantValidator extends AbstractValidator<HttpServletRequest> {

    public DeviceGrantValidator(){
        requiredParams.add("device_id");
        requiredParams.add("username");
    }
}
