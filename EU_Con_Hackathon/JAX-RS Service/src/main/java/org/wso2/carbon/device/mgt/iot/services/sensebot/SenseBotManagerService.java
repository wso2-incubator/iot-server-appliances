/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.iot.services.sensebot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.spi.DeviceMgtService;
import org.wso2.carbon.device.mgt.iot.arduino.firealarm.constants.FireAlarmConstants;
import org.wso2.carbon.device.mgt.iot.arduino.firealarm.constants.SenseBotConstants;
import org.wso2.carbon.device.mgt.iot.arduino.firealarm.impl.FireAlarmManager;
import org.wso2.carbon.device.mgt.iot.web.register.DeviceManagement;
import org.wso2.carbon.utils.CarbonUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//@Path("/FireAlarmDeviceManager")
public class SenseBotManagerService {

	private static Log log = LogFactory.getLog(SenseBotManagerService.class);

	@Path("/registerDevice")
	@PUT
	public boolean register(@QueryParam("deviceId") String deviceId, @QueryParam("name") String name,
			@QueryParam("owner") String owner)
			throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();

		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(deviceId);
		deviceIdentifier.setType(SenseBotConstants.DEVICE_TYPE);

		if (deviceManagement.isExist(deviceIdentifier)) {
			Response.status(409).build();
		}

		Device device = new Device();
		device.setDeviceIdentifier(deviceId);

		device.setDateOfEnrolment(new Date().getTime());
		device.setDateOfLastUpdate(new Date().getTime());
		//		device.setStatus(true);

		device.setName(name);
		device.setType(SenseBotConstants.DEVICE_TYPE);
		device.setDeviceTypeId(1);
		device.setOwner(owner);
		boolean added = deviceManagement.addNewDevice(device);

		return added;

	}

	@Path("/removeDevice")
	@DELETE
	public boolean removeDevice(@QueryParam("deviceId") String deviceId)
			throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();
		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(deviceId);
		deviceIdentifier.setType(SenseBotConstants.DEVICE_TYPE);

		boolean removed = deviceManagement.removeDevice(deviceIdentifier);
		return removed;


	}

	@Path("/updateDevice")
	@POST
	public boolean updateDevice(@QueryParam("deviceId") String deviceId,
			@QueryParam("name") String name)
			throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();

		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(deviceId);
		deviceIdentifier.setType(SenseBotConstants.DEVICE_TYPE);

		Device device = deviceManagement.getDevice(deviceIdentifier);
		device.setDeviceIdentifier(deviceId);

		// device.setDeviceTypeId(deviceTypeId);
		device.setDateOfLastUpdate(new Date().getTime());

		device.setName(name);
		device.setType(SenseBotConstants.DEVICE_TYPE);

		boolean updated = deviceManagement.update(device);
		return updated;

	}

	@Path("/getDevice")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public Device getDevice(@QueryParam("deviceId") String deviceId,
			@QueryParam("type") String type) throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();
		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(deviceId);
		deviceIdentifier.setType(SenseBotConstants.DEVICE_TYPE);


		Device device = deviceManagement.getDevice(deviceIdentifier);


		return device;
	}

	@Path("/downloadSketch")
	@GET
	@Produces("application/octet-stream")
	public Response downloadSketch(@QueryParam("owner") String owner,
			@QueryParam("type") String type)
			throws DeviceManagementException {

		if (owner == null || type == null) {
			return Response.status(400).build();//bad request
		}

		String a = SenseBotConstants.DEVICE_TYPE;
		/* create new device id */
		String deviceId = UUID.randomUUID().toString();

		String sep = File.separator;
		String sketchFolder = "repository" + sep + "resources" + sep + "sketches";
		String archivesPath = CarbonUtils.getCarbonHome() + sep + sketchFolder + sep + "archives"
				+ sep + deviceId;
		String templateSketchPath = sketchFolder + sep + type;

		Map<String, String> contextParams = new HashMap<String, String>();
		contextParams.put("DEVICE_OWNER", owner);
		contextParams.put("DEVICE_ID", deviceId);

		DeviceManagement deviceManagement = new DeviceManagement();
		File zipFile = deviceManagement.getSketchArchive(archivesPath, templateSketchPath,
				contextParams);

		Response.ResponseBuilder rb = Response.ok((Object) zipFile);
		rb.header("Content-Disposition", "attachment; filename=\"SenseBotAgent.zip\"");
		return rb.build();
	}



}
