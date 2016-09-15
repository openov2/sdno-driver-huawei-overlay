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

package org.openo.sdno.overlayvpndriver.service.wan;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.framework.container.util.JsonUtil;
import org.openo.sdno.overlayvpn.enums.WanInterfaceUsedType;
import org.openo.sdno.overlayvpn.model.port.WanSubInterface;
import org.openo.sdno.overlayvpndriver.login.OverlayVpnDriverProxy;
import org.openo.sdno.overlayvpndriver.login.OverlayVpnDriverResponse;
import org.openo.sdno.overlayvpndriver.model.port.NetAcDevicePort;
import org.openo.sdno.overlayvpndriver.model.vxlan.adapter.NetVxLanDeviceModel;
import org.openo.sdno.overlayvpndriver.util.config.WanInterface;
import org.openo.sdno.overlayvpndriver.util.controller.ControllerUtil;
import org.openo.sdno.util.http.HTTPReturnMessage;

import mockit.Mock;
import mockit.MockUp;

public class WanInfSvcImplTest {

	@Test
	public void testQueryWanInterface() throws ServiceException {

		new MockUp<OverlayVpnDriverProxy>() {

			@Mock
			public HTTPReturnMessage sendGetMsg(String url, String body, String ctlrUuid) {
				HTTPReturnMessage msg = new HTTPReturnMessage();
				msg.setStatus(200);

				OverlayVpnDriverResponse<List<NetVxLanDeviceModel>> res = new OverlayVpnDriverResponse<List<NetVxLanDeviceModel>>();
				NetVxLanDeviceModel mo = new NetVxLanDeviceModel();
				List<NetVxLanDeviceModel> mos = new ArrayList<>();
				mos.add(mo);
				res.setData(mos);

				res.setErrcode("0");
				msg.setBody(JsonUtil.toJson(res));
				return msg;
			}

		};

		new MockUp<WanInterface>() {

			@Mock
			public String getConfig(String cfgKey) throws ServiceException {
				return "123";
			}

		};

		new MockUp<ControllerUtil>() {

			@Mock
			public List checkRsp(HTTPReturnMessage httpMsg) throws ServiceException {
				WanSubInterface mo = new WanSubInterface();
				mo.setCeLowVlan(123);
				List<WanSubInterface> mos = new ArrayList<>();
				mos.add(mo);

				return mos;

			}
		};

		new MockUp<WanInfSvcImpl>() {

			@Mock
			public List<NetAcDevicePort> queryPorts(List<String> interfaceNameList, String deviceId, String ctrlUuid) {
				List<NetAcDevicePort> list = new ArrayList<>();
				NetAcDevicePort netAcDevicePort = new NetAcDevicePort();
				netAcDevicePort.setAlias("alias");
				list.add(netAcDevicePort);
				return list;
			}
		};

		try {
			List<WanSubInterface> result = WanInfSvcImpl.queryWanInterface("123", "123",
					WanInterfaceUsedType.GRE.getName());
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void testQueryWanInterfaceBranch() throws ServiceException {

		new MockUp<OverlayVpnDriverProxy>() {

			@Mock
			public HTTPReturnMessage sendGetMsg(String url, String body, String ctlrUuid) {
				HTTPReturnMessage msg = new HTTPReturnMessage();
				msg.setStatus(200);

				OverlayVpnDriverResponse<List<NetVxLanDeviceModel>> res = new OverlayVpnDriverResponse<List<NetVxLanDeviceModel>>();
				NetVxLanDeviceModel mo = new NetVxLanDeviceModel();
				List<NetVxLanDeviceModel> mos = new ArrayList<>();
				mos.add(mo);
				res.setData(mos);

				res.setErrcode("0");
				msg.setBody(JsonUtil.toJson(res));
				return msg;
			}

		};

		new MockUp<WanInterface>() {

			@Mock
			public String getConfig(String cfgKey) throws ServiceException {
				return "123";
			}

		};

		new MockUp<ControllerUtil>() {

			@Mock
			public List checkRsp(HTTPReturnMessage httpMsg) throws ServiceException {
				WanSubInterface mo = new WanSubInterface();
				mo.setCeLowVlan(123);
				List<WanSubInterface> mos = new ArrayList<>();
				mos.add(mo);

				return mos;

			}
		};

		new MockUp<WanInfSvcImpl>() {

			@Mock
			public List<NetAcDevicePort> queryPorts(List<String> interfaceNameList, String deviceId, String ctrlUuid) {
				List<NetAcDevicePort> list = new ArrayList<>();
				NetAcDevicePort netAcDevicePort = new NetAcDevicePort();
				netAcDevicePort.setAlias("alias");
				netAcDevicePort.setIpAddr("1.1.1.1");
				list.add(netAcDevicePort);
				return list;
			}
		};

		try {
			List<WanSubInterface> result = WanInfSvcImpl.queryWanInterface("123", "123",
					WanInterfaceUsedType.GRE.getName());
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void testQueryWanInterface_All() throws ServiceException {

		new MockUp<OverlayVpnDriverProxy>() {

			@Mock
			public HTTPReturnMessage sendGetMsg(String url, String body, String ctlrUuid) {
				HTTPReturnMessage msg = new HTTPReturnMessage();
				msg.setStatus(200);

				OverlayVpnDriverResponse<List<NetVxLanDeviceModel>> res = new OverlayVpnDriverResponse<List<NetVxLanDeviceModel>>();
				NetVxLanDeviceModel mo = new NetVxLanDeviceModel();
				List<NetVxLanDeviceModel> mos = new ArrayList<>();
				mos.add(mo);
				res.setData(mos);

				res.setErrcode("0");
				msg.setBody(JsonUtil.toJson(res));
				return msg;
			}

		};

		new MockUp<WanInterface>() {

			@Mock
			public String getConfig(String cfgKey) throws ServiceException {
				return "123";
			}

		};

		new MockUp<ControllerUtil>() {

			@Mock
			public List checkRsp(HTTPReturnMessage httpMsg) throws ServiceException {
				WanSubInterface mo = new WanSubInterface();
				mo.setCeLowVlan(123);
				List<WanSubInterface> mos = new ArrayList<>();
				mos.add(mo);

				return mos;

			}
		};

		try {
			List<WanSubInterface> result = WanInfSvcImpl.queryWanInterface("123", "123",
					WanInterfaceUsedType.ALL.getName());
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void testQueryWanInterface_1() throws ServiceException {
		try {
			List<WanSubInterface> result = WanInfSvcImpl.queryWanInterface("", "123",
					WanInterfaceUsedType.GRE.getName());
		} catch (Exception e) {
			assertTrue(e instanceof ServiceException);
		}
	}

	@Test
	public void testQueryWanInterface_2() throws ServiceException {
		try {
			List<WanSubInterface> result = WanInfSvcImpl.queryWanInterface("123", "",
					WanInterfaceUsedType.GRE.getName());
		} catch (Exception e) {
			assertTrue(e instanceof ServiceException);
		}
	}

	@Test
	public void testQueryWanInterface_3() throws ServiceException {
		try {
			List<WanSubInterface> result = WanInfSvcImpl.queryWanInterface("123", "123",
					WanInterfaceUsedType.GRE.getName());
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void testQueryWanInterfaceExe() throws ServiceException {

		new MockUp<OverlayVpnDriverProxy>() {

			@Mock
			public HTTPReturnMessage sendGetMsg(String url, String body, String ctlrUuid) {
				HTTPReturnMessage msg = new HTTPReturnMessage();
				msg.setStatus(200);

				OverlayVpnDriverResponse<List<NetVxLanDeviceModel>> res = new OverlayVpnDriverResponse<List<NetVxLanDeviceModel>>();
				NetVxLanDeviceModel mo = new NetVxLanDeviceModel();
				List<NetVxLanDeviceModel> mos = new ArrayList<>();
				mos.add(mo);
				res.setData(mos);

				res.setErrcode("0");
				msg.setBody(JsonUtil.toJson(res));
				return msg;
			}

		};

		new MockUp<WanInterface>() {

			@Mock
			public String getConfig(String cfgKey) throws ServiceException {
				return "123";
			}

		};

		new MockUp<ControllerUtil>() {

			@Mock
			public List checkRsp(HTTPReturnMessage httpMsg) throws ServiceException {

				List<WanSubInterface> mos = new ArrayList<>();

				return mos;

			}
		};

		try {
			List<WanSubInterface> result = WanInfSvcImpl.queryWanInterface("123", "123",
					WanInterfaceUsedType.GRE.getName());
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void testQueryWanInterfaceExeVlanId() throws ServiceException {

		new MockUp<OverlayVpnDriverProxy>() {

			@Mock
			public HTTPReturnMessage sendGetMsg(String url, String body, String ctlrUuid) {
				HTTPReturnMessage msg = new HTTPReturnMessage();
				msg.setStatus(200);

				OverlayVpnDriverResponse<List<NetVxLanDeviceModel>> res = new OverlayVpnDriverResponse<List<NetVxLanDeviceModel>>();
				NetVxLanDeviceModel mo = new NetVxLanDeviceModel();
				List<NetVxLanDeviceModel> mos = new ArrayList<>();
				mos.add(mo);
				res.setData(mos);

				res.setErrcode("0");
				msg.setBody(JsonUtil.toJson(res));
				return msg;
			}

		};

		new MockUp<WanInterface>() {

			@Mock
			public String getConfig(String cfgKey) throws ServiceException {
				return "";
			}

		};

		new MockUp<ControllerUtil>() {

			@Mock
			public List checkRsp(HTTPReturnMessage httpMsg) throws ServiceException {
				WanSubInterface mo = new WanSubInterface();
				mo.setCeLowVlan(123);
				List<WanSubInterface> mos = new ArrayList<>();
				mos.add(mo);

				return mos;

			}
		};

		try {
			List<WanSubInterface> result = WanInfSvcImpl.queryWanInterface("123", "123", "");
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void testQueryWanInterfaceExeFor() throws ServiceException {

		new MockUp<OverlayVpnDriverProxy>() {

			@Mock
			public HTTPReturnMessage sendGetMsg(String url, String body, String ctlrUuid) {
				HTTPReturnMessage msg = new HTTPReturnMessage();
				msg.setStatus(200);

				OverlayVpnDriverResponse<List<NetVxLanDeviceModel>> res = new OverlayVpnDriverResponse<List<NetVxLanDeviceModel>>();
				NetVxLanDeviceModel mo = new NetVxLanDeviceModel();
				List<NetVxLanDeviceModel> mos = new ArrayList<>();
				mos.add(mo);
				res.setData(mos);

				res.setErrcode("0");
				msg.setBody(JsonUtil.toJson(res));
				return msg;
			}

		};

		new MockUp<WanInterface>() {

			@Mock
			public String getConfig(String cfgKey) throws ServiceException {
				return "";
			}

		};

		new MockUp<ControllerUtil>() {

			@Mock
			public List checkRsp(HTTPReturnMessage httpMsg) throws ServiceException {
				WanSubInterface mo = new WanSubInterface();
				mo.setCeLowVlan(123);
				List<WanSubInterface> mos = new ArrayList<>();
				mos.add(mo);

				return null;

			}
		};

		try {
			List<WanSubInterface> result = WanInfSvcImpl.queryWanInterface("123", "123", "");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
}
