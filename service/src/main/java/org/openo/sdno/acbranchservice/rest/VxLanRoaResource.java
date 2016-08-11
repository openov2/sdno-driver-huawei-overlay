/*
 * Copyright (c) 2016, Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openo.sdno.acbranchservice.rest;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.acbranchservice.model.vxlan.adapter.NetVxLanDeviceModel;
import org.openo.sdno.acbranchservice.service.vxlan.VxLanSvcImpl;
import org.openo.sdno.acbranchservice.service.wan.WanInfSvcImpl;
import org.openo.sdno.acbranchservice.util.convertmodel.VxLanModelConvert;
import org.openo.sdno.overlayvpn.enums.WanInterfaceUsedType;
import org.openo.sdno.overlayvpn.errorcode.ErrorCode;
import org.openo.sdno.overlayvpn.model.netmodel.vxlan.NeVtep;
import org.openo.sdno.overlayvpn.model.netmodel.vxlan.NeVxlanInstance;
import org.openo.sdno.overlayvpn.model.port.WanSubInterface;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.openo.sdno.overlayvpn.result.SvcExcptUtil;
import org.openo.sdno.overlayvpn.util.check.CheckStrUtil;
import org.openo.sdno.overlayvpn.util.check.UuidUtil;
import org.openo.sdno.overlayvpn.util.check.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Restful interface class for VxLan. <br/>
 *
 * @author
 * @version SDNO 0.5 Jul 19, 2016
 */
@Service
@Path("/svc/sbiadp/controller")
public class VxLanRoaResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(VxLanRoaResource.class);

    /**
     * Query vtep information. <br/>
     *
     * @param ctrlUuid Controller UUID
     * @param deviceId The device id
     * @return ResultRsp object with NeVtep
     * @throws ServiceException When query failed
     * @since SDNO 0.5
     */
    @GET
    @Path("/{ctrluuid}/v1/vxlan/device/{deviceid}/vtep")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResultRsp<NeVtep> queryVtep(@PathParam("ctrluuid") String ctrlUuid, @PathParam("deviceid") String deviceId)
            throws ServiceException {

        long beginTime = System.currentTimeMillis();

        // check ctrlUuid
        if(!UuidUtil.validate(ctrlUuid)) {
            LOGGER.error("queryVtep failed, ctrlUuid is invalid.");
            SvcExcptUtil.throwBadRequestException("queryVtep failed, ctrlUuid is invalid");
        }

        // check deviceId
        CheckStrUtil.checkUuidStr(deviceId);

        // get WanSubInterface
        List<WanSubInterface> wanSubInterfaceList =
                WanInfSvcImpl.queryWanInterface(ctrlUuid, deviceId, WanInterfaceUsedType.VXLAN.getName());
        if(CollectionUtils.isEmpty(wanSubInterfaceList)
                || StringUtils.isEmpty(wanSubInterfaceList.get(0).getIpAddress())) {
            return new ResultRsp<NeVtep>(ErrorCode.OVERLAYVPN_FAILED, "queryVtep failed, can't get WanSubInterface",
                    null, null, null);
        }

        WanSubInterface wanSubif = wanSubInterfaceList.get(0);

        LOGGER.info("queryVtep cost time = " + (System.currentTimeMillis() - beginTime));

        return new ResultRsp<NeVtep>(ErrorCode.OVERLAYVPN_SUCCESS, new NeVtep(deviceId, wanSubif.getIpAddress()));
    }

    /**
     * Create VxLan instance. <br/>
     *
     * @param request HTTP request
     * @param ctrlUuid Controller UUID
     * @param vxLanInstanceList Collection list of VxLan
     * @return ResultRsp object with VxLan list data
     * @throws ServiceException When create failed
     * @since SDNO 0.5
     */
    @POST
    @Path("/{ctrluuid}/v1/vxlan/batch-create-vxlan")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResultRsp<List<NeVxlanInstance>> createVxlan(@Context HttpServletRequest request,
            @PathParam("ctrluuid") String ctrlUuid, List<NeVxlanInstance> vxLanInstanceList) throws ServiceException {
        long beginTime = System.currentTimeMillis();

        // check parameters
        if(!UuidUtil.validate(ctrlUuid)) {
            LOGGER.error("createVxlan failed, ctrlUuid is invalid.");
            SvcExcptUtil.throwBadRequestException("createVxlan failed, ctrlUuid is invalid");
        }

        if(CollectionUtils.isEmpty(vxLanInstanceList)) {
            LOGGER.error("createVxlan failed, vxLanInstanceList is null");
            SvcExcptUtil.throwBadRequestException("createVxlan failed, vxlanInstanceList is null");
        }

        for(NeVxlanInstance tempVxlan : vxLanInstanceList) {
            ValidationUtil.validateModel(tempVxlan);
            if(CollectionUtils.isEmpty(tempVxlan.getVxlanInterfaceList())) {
                LOGGER.error("createVxlan failed, vxlanInterfaceList is null");
                SvcExcptUtil.throwBadRequestException("createVxlan failed, vxlanInterfaceList is null");
            }

            if(CollectionUtils.isEmpty(tempVxlan.getVxlanTunnelList())) {
                LOGGER.error("createVxlan failed, vxlanTunnelList is null");
                SvcExcptUtil.throwBadRequestException("createVxlan failed, vxlanTunnelList is null");
            }
        }

        // convert model from IpSecService to adapter
        Map<String, List<NetVxLanDeviceModel>> vxlanDeviceModelMap = VxLanModelConvert.convertModel(vxLanInstanceList);

        // call the service method to perform create operation
        for(Map.Entry<String, List<NetVxLanDeviceModel>> entry : vxlanDeviceModelMap.entrySet()) {
            ResultRsp<List<NetVxLanDeviceModel>> resultRsp =
                    VxLanSvcImpl.createVxLan(ctrlUuid, entry.getKey(), entry.getValue());
            if(!resultRsp.isSuccess()) {
                LOGGER.error("createVxlan failed in service");
                return new ResultRsp<List<NeVxlanInstance>>(resultRsp, vxLanInstanceList);
            }
        }

        LOGGER.info("createVxlan cost time = " + (System.currentTimeMillis() - beginTime));

        return new ResultRsp<List<NeVxlanInstance>>(ErrorCode.OVERLAYVPN_SUCCESS, vxLanInstanceList);
    }

    /**
     * Delete VxLan instance. <br/>
     * 
     * @param request HTTP request
     * @param ctrlUuid Controller UUID
     * @param instanceId The UUID of VxLan instance
     * @return The object of ResultRsp
     * @throws ServiceException When delete failed
     * @since SDNO 0.5
     */
    @DELETE
    @Path("/{ctrluuid}/v1/vxlan/instance/{instanceid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResultRsp<String> deleteVxLan(@Context HttpServletRequest request, @PathParam("ctrluuid") String ctrlUuid,
            @PathParam("instanceid") String instanceId) throws ServiceException {
        long beginTime = System.currentTimeMillis();

        // check parameters
        if(!UuidUtil.validate(ctrlUuid)) {
            LOGGER.error("deleteVxLan falied, ctrlUuid is invalid.");
            SvcExcptUtil.throwBadRequestException("deleteVxLan falied, ctrlUuid is invalid");
        }

        // call the service method to perform delete operation
        ResultRsp<String> resultRsp = VxLanSvcImpl.deleteVxLan(ctrlUuid, instanceId);

        LOGGER.info("deleteVxLan cost time = " + (System.currentTimeMillis() - beginTime));

        return resultRsp;
    }
}
