package org.wso2.iot.firealarm.access.api;

public class Device {

	private String id;
	private String name;
	Device(String id,String name){
		this.id=id;
		this.name=name;

	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


}
