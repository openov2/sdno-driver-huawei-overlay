/*
 * Copyright 2016 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openo.sdno.overlayvpndriver.login;

import java.net.MalformedURLException;
import java.net.URL;

import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.exception.HttpCode;
import org.openo.sdno.overlayvpn.esr.invdao.SdnControllerDao;
import org.openo.sdno.overlayvpn.esr.model.SdnController;
import org.openo.sdno.util.http.HTTPReturnMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Proxy class for AC Branch Controller, providing restful and web socket interface. <br>
 * 
 * @author
 * @version SDNO 0.5 Jul 21, 2016
 */
public class OverlayVpnDriverProxy implements IOverlayVpnDriverProxy {

    private static final String SSO_LOGIN = "/sso/login";

    private static final String SSO_REDIRECT = "ssoRedirect";

    private static final String SSO_LOGOUT = "sso/logout";

    private static final Logger LOGGER = LoggerFactory.getLogger(OverlayVpnDriverProxy.class);

    private static volatile OverlayVpnDriverProxy uniqueInstance = null;

    private OverlayVpnDriverProxy() {
    }

    /**
     * Get synchronized instance. <br>
     *
     * @return synchronized instance
     * @since SDNO 0.5
     */
    public static synchronized OverlayVpnDriverProxy getInstance() {
        if(null == uniqueInstance) {
            uniqueInstance = new OverlayVpnDriverProxy();
        }
        return uniqueInstance;
    }

    /**
     * Send get restful request.<br>
     *
     * @param url The restful URL path
     * @param body The message body
     * @param ctlrUuid The controller UUID
     * @return The object of HTTPReturnMessage
     * @since SDNO 0.5
     */
    @Override
    public HTTPReturnMessage sendGetMsg(String url, String body, String ctlrUuid) {
        OverlayVpnDriverSsoProxy acSSOLogin = createACSSOProxy(ctlrUuid);
        if(null == acSSOLogin) {
            return new HTTPReturnMessage();
        }

        HTTPReturnMessage get = acSSOLogin.get(url);
        if(isNeedRelogin(get)) {
            LOGGER.info("Not Login, try to login.");
            if(acSSOLogin.login(SSO_LOGIN)) {
                get = acSSOLogin.get(url);
            } else {
                get.setStatus(HttpCode.ERR_FAILED);
            }
        }

        return get;
    }

    /**
     * Send post restful request.<br>
     *
     * @param url The restful URL path
     * @param body The message body
     * @param ctlrUuid The controller UUID
     * @return The object of HTTPReturnMessage
     * @since SDNO 0.5
     */
    @Override
    public HTTPReturnMessage sendPostMsg(String url, String body, String ctlrUuid) {
        OverlayVpnDriverSsoProxy acSSOLogin = createACSSOProxy(ctlrUuid);
        if(null == acSSOLogin) {
            return new HTTPReturnMessage();
        }

        HTTPReturnMessage post = acSSOLogin.post(url, body);
        if(isNeedRelogin(post)) {
            LOGGER.info("Not Login, try to login.");
            if(acSSOLogin.login(SSO_LOGIN)) {
                post = acSSOLogin.post(url, body);
            } else {
                post.setStatus(HttpCode.ERR_FAILED);
            }
        }

        return post;
    }

    /**
     * Send put restful request.<br>
     *
     * @param url The restful URL path
     * @param body The message body
     * @param ctlrUuid The controller UUID
     * @return The object of HTTPReturnMessage
     * @since SDNO 0.5
     */
    @Override
    public HTTPReturnMessage sendPutMsg(String url, String body, String ctlrUuid) {
        OverlayVpnDriverSsoProxy acSSOLogin = createACSSOProxy(ctlrUuid);
        if(null == acSSOLogin) {
            return new HTTPReturnMessage();
        }

        HTTPReturnMessage put = acSSOLogin.put(url, body);
        if(isNeedRelogin(put)) {
            LOGGER.info("Not Login, try to login.");
            if(acSSOLogin.login(SSO_LOGIN)) {
                put = acSSOLogin.put(url, body);
            } else {
                put.setStatus(HttpCode.ERR_FAILED);
            }
        }

        return put;
    }

    /**
     * Send delete restful request.<br>
     *
     * @param url The restful URL path
     * @param body The message body
     * @param ctlrUuid The controller UUID
     * @return The object of HTTPReturnMessage
     * @since SDNO 0.5
     */
    @Override
    public HTTPReturnMessage sendDeleteMsg(String url, String body, String ctlrUuid) {
        OverlayVpnDriverSsoProxy acSSOLogin = createACSSOProxy(ctlrUuid);
        if(null == acSSOLogin) {
            return new HTTPReturnMessage();
        }

        HTTPReturnMessage delete = acSSOLogin.delete(url, body);
        if(isNeedRelogin(delete)) {
            LOGGER.info("Not Login, try to login.");
            if(acSSOLogin.login(SSO_LOGIN)) {
                delete = acSSOLogin.delete(url, body);
            } else {
                delete.setStatus(HttpCode.ERR_FAILED);
            }
        }

        return delete;
    }

    private boolean isNeedRelogin(HTTPReturnMessage httpRspMsg) {
        return isRedirectResponse(httpRspMsg) || isLogoutResponse(httpRspMsg);
    }

    private boolean isLogoutResponse(HTTPReturnMessage httpRspMsg) {
        return (null != httpRspMsg.getBody()) && (httpRspMsg.getBody().indexOf(SSO_LOGOUT) > 0);
    }

    private boolean isRedirectResponse(HTTPReturnMessage httpRspMsg) {
        return (null != httpRspMsg.getBody()) && (httpRspMsg.getBody().indexOf(SSO_REDIRECT) > 0);
    }

    private OverlayVpnDriverSsoProxy createACSSOProxy(String ctlrUuid) {
        OverlayVpnDriverSsoProxy ssoProxy = null;

        try {
            SdnControllerDao controllerDao = new SdnControllerDao();
            SdnController sdnController = controllerDao.querySdnControllerById(ctlrUuid);
            URL url = new URL(sdnController.getUrl());
            ssoProxy = OverlayVpnDriverSsoProxy.getInstance(url.getHost(), String.valueOf(url.getPort()),
                    sdnController.getUserName(), sdnController.getPassword());
        } catch(ServiceException | MalformedURLException e) {
            LOGGER.error("create OverlayVpnDriverSsoProxy faied", e);
        }

        return ssoProxy;
    }
}
