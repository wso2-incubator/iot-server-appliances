package org.wso2.carbon.devicemgt.grant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;

/**
 * Created by ace on 7/3/15.
 */
public class DeviceGrant extends AbstractAuthorizationGrantHandler {

    private static Log log = LogFactory.getLog(AbstractAuthorizationGrantHandler.class);

    private static final String DEVICE_ID = "device_id";
    private static final String USER_NAME = "username";

    @Override
    public boolean validateGrant(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {

        RequestParameter[] parameters =  tokReqMsgCtx.getOauth2AccessTokenReqDTO().getRequestParameters();
        boolean result = super.validateGrant(tokReqMsgCtx);

        int tenantId = tokReqMsgCtx.getTenantID();
        String tenantDomain = MultitenantConstants.SUPER_TENANT_DOMAIN_NAME;
        if (tenantId != MultitenantConstants.SUPER_TENANT_ID) {
                tenantDomain = CarbonContext.getThreadLocalCarbonContext().getTenantDomain();
        }
        String username = null;
        String deviceId = null;

        for(RequestParameter parameter : parameters){
            if(DEVICE_ID.equals(parameter.getKey())){
                if(parameter.getValue() != null && parameter.getValue().length > 0){
                    if(parameter.getValue()[0] == "0"){
                        deviceId = null;
                    }else{
                        deviceId = parameter.getValue()[0];
                    }

                }
            }else if(USER_NAME.equals(parameter.getKey())){
                if(parameter.getValue() != null && parameter.getValue().length > 0){
                    if(parameter.getValue()[0] == "0"){
                        username = null;
                    }else{
                        username = parameter.getValue()[0];
                    }

                }
            }
        }

        if(deviceId == null || username == null){
            return  false;
        }

        username = username + "@" + tenantDomain;

        tokReqMsgCtx.setAuthorizedUser(username.concat(":").concat(deviceId));

        return true;
    }

    @Override
    public boolean authorizeAccessDelegation(OAuthTokenReqMessageContext var1) throws IdentityOAuth2Exception{
        return true;
    }

    @Override
    public boolean validateScope(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {
        return true;
    }


}
