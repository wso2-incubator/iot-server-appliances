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

package org.wso2.carbon.device.mgt.iot.services.firealarm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.arduino.firealarm.constants.FireAlarmConstants;
import org.wso2.carbon.device.mgt.iot.services.common.DevicesManager;
import org.wso2.carbon.device.mgt.iot.web.register.DeviceManagement;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.UUID;

//@Path("/FireAlarmDeviceManager")
public class FireAlarmManagerService {

	private static Log log = LogFactory.getLog(FireAlarmManagerService.class);

	@Path("/registerDevice")
	@PUT
	public boolean register(@QueryParam("deviceId") String deviceId,
							@QueryParam("name") String name, @QueryParam("owner") String owner)
			throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();

		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(deviceId);
		deviceIdentifier.setType(FireAlarmConstants.DEVICE_TYPE);

		if (deviceManagement.isExist(deviceIdentifier)) {
			Response.status(409).build();
			return false;
		}

		Device device = new Device();
		device.setDeviceIdentifier(deviceId);

		device.setDateOfEnrolment(new Date().getTime());
		device.setDateOfLastUpdate(new Date().getTime());
		//		device.setStatus(true);

		device.setName(name);
		device.setType(FireAlarmConstants.DEVICE_TYPE);
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
		deviceIdentifier.setType(FireAlarmConstants.DEVICE_TYPE);

		boolean removed = deviceManagement.removeDevice(deviceIdentifier);
		return removed;

	}

	@Path("/updateDevice")
	@POST
	public boolean updateDevice(@QueryParam("deviceId") String deviceId,
								@QueryParam("name") String name) throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();

		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(deviceId);
		deviceIdentifier.setType(FireAlarmConstants.DEVICE_TYPE);

		Device device = deviceManagement.getDevice(deviceIdentifier);
		device.setDeviceIdentifier(deviceId);

		// device.setDeviceTypeId(deviceTypeId);
		device.setDateOfLastUpdate(new Date().getTime());

		device.setName(name);
		device.setType(FireAlarmConstants.DEVICE_TYPE);

		boolean updated = deviceManagement.update(device);
		return updated;

	}

	@Path("/getDevice")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public Device getDevice(@QueryParam("deviceId") String deviceId) {

		DeviceManagement deviceManagement = new DeviceManagement();
		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(deviceId);
		deviceIdentifier.setType(FireAlarmConstants.DEVICE_TYPE);

		try {
			Device device = deviceManagement.getDevice(deviceIdentifier);

			return device;
		} catch (DeviceManagementException ex) {
			log.error("Error occurred while retrieving device with Id " + deviceId);
			return null;
		}

	}

	@Path("/downloadSketch")
	@GET
	@Produces("application/octet-stream")
	public Response downloadSketch(@QueryParam("owner") String owner, @QueryParam("type") String
			sketchType) {

		if (owner == null) {
			return Response.status(400).build();//bad request
		}

		//create new device id
		String deviceId = shortUUID();

		//create token
		String token = UUID.randomUUID().toString();

		//adding registering data
		try {
			register(deviceId,
					 owner + "s_" + sketchType + "_" + deviceId.substring(0, 3),
					 owner);
		} catch (DeviceManagementException ex) {
			return Response.status(500).entity(
					"Error occurred while registering the device with " + "id: " + deviceId
							+ " owner:" + owner).build();
		}

		DevicesManager devicesManager = new DevicesManager();
		File zipFile = null;
		try {
			zipFile = devicesManager.downloadSketch(owner, sketchType, deviceId,
													token);
		} catch (DeviceManagementException ex) {
			return Response.status(500).entity("Error occurred while creating zip file").build();
		}

		Response.ResponseBuilder rb = Response.ok((Object) zipFile);
		rb.header("Content-Disposition", "attachment; filename=\"FireAlarmAgent.zip\"");
		return rb.build();
	}

	private static String shortUUID() {
		UUID uuid = UUID.randomUUID();
		long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
		return Long.toString(l, Character.MAX_RADIX);
	}

}
