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

package org.wso2.iot.device;

/**
 * @author smean-MAC
 *
 */
public class Device {
	private String id;
	private String name;
	private String type;
	private String model;
	private String mac_address;
	private String enrolledOn;
	private String desciption;
	private String owner;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}
	/**
	 * @return the mac_address
	 */
	public String getMac_address() {
		return mac_address;
	}
	/**
	 * @param mac_address the mac_address to set
	 */
	public void setMac_address(String mac_address) {
		this.mac_address = mac_address;
	}
	/**
	 * @return the enrolledOn
	 */
	public String getEnrolledOn() {
		return enrolledOn;
	}
//	/**
//	 * @param enrolledOn the enrolledOn to set
//	 */
//	public void setEnrolledOn(String enrolledOn) {
//		this.enrolledOn = enrolledOn;
//	}
	/**
	 * @return the desciption
	 */
	public String getDesciption() {
		return desciption;
	}
	/**
	 * @param desciption the desciption to set
	 */
	public void setDesciption(String desciption) {
		this.desciption = desciption;
	}
	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}
	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	
	
}
