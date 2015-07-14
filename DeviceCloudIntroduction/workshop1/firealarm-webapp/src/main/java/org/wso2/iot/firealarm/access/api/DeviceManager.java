package org.wso2.iot.firealarm.access.api;

import java.util.List;

public class DeviceManager {

	public List<Device> getDevice(String token,String username){

		FirealarmClient fc=new FirealarmClient();
		List<Device> devices=fc.getDevice(token, username);
		return devices;
	}
}
