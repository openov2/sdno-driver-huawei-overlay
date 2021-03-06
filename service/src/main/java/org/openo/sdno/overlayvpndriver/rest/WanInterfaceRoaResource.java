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

package org.openo.sdno.overlayvpndriver.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.overlayvpn.enums.WanInterfaceUsedType;
import org.openo.sdno.overlayvpn.errorcode.ErrorCode;
import org.openo.sdno.overlayvpn.model.port.WanSubInterface;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.openo.sdno.overlayvpn.result.SvcExcptUtil;
import org.openo.sdno.overlayvpn.util.check.CheckStrUtil;
import org.openo.sdno.overlayvpn.util.check.UuidUtil;
import org.openo.sdno.overlayvpndriver.service.wan.WanInfSvcImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Restful interface class for WanInterface.<br>
 *
 * @author
 * @version SDNO 0.5 Jul 21, 2016
 */
@Service
@Path("/sbi-waninterface/v1")
public class WanInterfaceRoaResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(WanInterfaceRoaResource.class);

    @Autowired
    private WanInfSvcImpl wanInfSvc;

    /**
     * Query WanInterface information. <br>
     *
     * @param ctrlUuid Controller UUID
     * @param deviceId The device id
     * @param type The WanInterface type that want to get
     * @return ResultRsp list of WanSubInterface
     * @throws WebApplicationException When WanSubInterface query failed
     * @since SDNO 0.5
     */
    @GET
    @Path("/overlay/device/{deviceid}/wan-sub-interfaces")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResultRsp<List<WanSubInterface>> queryWanInterface(@HeaderParam("X-Driver-Parameter") String ctrlUuidParam,
            @PathParam("deviceid") String deviceId, @QueryParam("type") String type) throws WebApplicationException {

        long beginTime = System.currentTimeMillis();
        String ctrlUuid = ctrlUuidParam.substring(ctrlUuidParam.indexOf('=') + 1);

        try {
            // check parameters
            if(!UuidUtil.validate(ctrlUuid)) {
                LOGGER.error("queryWanInterface failed, ctrlUuid is invalid");
                SvcExcptUtil.throwBadRequestException("queryWanInterface failed, ctrlUuid is invalid");
            }

            CheckStrUtil.checkUuidStr(deviceId);

            if(!WanInterfaceUsedType.isValid(type)) {
                LOGGER.error("queryWanInterface failed, query parameter type is error");
                SvcExcptUtil.throwBadRequestException("queryWanInterface failed, query parameter type is error");
            }

            // call the service method to perform query operation
            List<WanSubInterface> wanSubInterfaceList = wanInfSvc.queryWanInterface(ctrlUuid, deviceId, type);

            LOGGER.info("queryWanInterface cost time = " + (System.currentTimeMillis() - beginTime));

            return new ResultRsp<List<WanSubInterface>>(ErrorCode.OVERLAYVPN_SUCCESS, wanSubInterfaceList);
        } catch(ServiceException e) {
            throw new WebApplicationException(e.getId(), e.getHttpCode());
        }
    }
}
