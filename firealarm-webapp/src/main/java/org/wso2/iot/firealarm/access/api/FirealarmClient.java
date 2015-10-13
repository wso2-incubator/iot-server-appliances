package org.wso2.iot.firealarm.access.api;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class FirealarmClient {

//	private String endpoint="http://192.168.57.128:8281/firealarm/1.0";
	private String endpoint="http://localhost:8281/virtual_firealarm/1.0";
	private String bulbContext="/controller/bulb/";
	private String temperatureContext="/controller/readtemperature";
	private String humidityContext="/controller/readsonar";
	private String deviceContext="/manager/devices/";

	public String switchBulb(String accessToken,String username,String deviceId,String state, String protocol){
		HttpClient httpClient = new HttpClient();
		PostMethod postMethod = new PostMethod(endpoint+bulbContext+state);
		postMethod.addRequestHeader("owner", username);
		postMethod.addRequestHeader("deviceId", deviceId);
		postMethod.addRequestHeader("protocol", protocol);
		postMethod.addRequestHeader("Authorization",
									"Bearer " + accessToken);
//		postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		try {
			httpClient.executeMethod(postMethod);
			int statusCode = postMethod.getStatusCode();
			if(statusCode==200){
				return "Successfully Sent";

			}

			return "Failed, Try again";
		} catch (IOException e) {
			return "Connection Failure,Try again later";
		}

	}


	public String getTemperature(String accessToken,String username,String deviceId, String protocol){
		HttpClient httpClient = new HttpClient();

		GetMethod getMethod =new GetMethod(endpoint+temperatureContext);


		getMethod.addRequestHeader("owner", username);
		getMethod.addRequestHeader("deviceId", deviceId);
		getMethod.addRequestHeader("protocol", protocol);
		getMethod.addRequestHeader("Authorization",
									"Bearer " + accessToken);
		//getMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		try {
			httpClient.executeMethod(getMethod);
			return getMethod.getResponseBodyAsString();
		} catch (IOException e) {
			return "Connection Failure,Try again later";
		}
	}


	public String getHumidity(String accessToken,String username,String deviceId, String protocol){
		HttpClient httpClient = new HttpClient();

		GetMethod getMethod =new GetMethod(endpoint+humidityContext);


		getMethod.addRequestHeader("owner", username);
		getMethod.addRequestHeader("deviceId", deviceId);
		getMethod.addRequestHeader("protocol", protocol);
		getMethod.addRequestHeader("Authorization",
		                           "Bearer " + accessToken);
		//getMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		try {
			httpClient.executeMethod(getMethod);
			return getMethod.getResponseBodyAsString();
		} catch (IOException e) {
			return "Connection Failure,Try again later";
		}
	}

	public ArrayList<Device> getDevice(String accessToken,String username){
		HttpClient httpClient = new HttpClient();
		GetMethod getMethod =new GetMethod(endpoint+deviceContext+username);

		getMethod.addRequestHeader("Authorization",
									"Bearer " + accessToken);
		getMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");


		try {
			httpClient.executeMethod(getMethod);
			String response = getMethod.getResponseBodyAsString();
			JSONArray jsonArray = new JSONArray(response);
			ArrayList<Device> deviceArray = new ArrayList<Device>();
			if (jsonArray != null) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					deviceArray.add(new Device(jsonObject.getString("deviceIdentifier"),
											   jsonObject.getString("name")));
				}
			}
			return deviceArray;
		}catch(JSONException e){
			return null;
		} catch (IOException e) {
			return null;
		}

	}


}
