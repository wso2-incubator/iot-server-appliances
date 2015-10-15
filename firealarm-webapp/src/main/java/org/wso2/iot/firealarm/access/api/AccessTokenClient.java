
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

package org.wso2.iot.firealarm.access.api;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;


public class AccessTokenClient {

	//TODO read from configuration file
	private static Log log = LogFactory.getLog(AccessTokenClient.class);
	private static AccessTokenClient accessTokenClient = new AccessTokenClient();
	private static final String propertiesFileName = "firealarmApp.properties";
	private static final String confDirPath = System.getProperty("carbon.config.dir.path");
	private static final String propertiesFilePath = confDirPath + File.separator + "iot" + File.separator + propertiesFileName;

	private static Properties properties = new Properties();;
	private static InputStream propertiesInputStream = null;

//	private String tokenURL ="https://192.168.57.128:9444/oauth2/token";
	private static String tokenURL ="https://localhost:9443/oauth2/token";
	private static String grantType ="password";
	private static String scope ="PRODUCTION device_";
	private static String appToken="NVp6UzIxM1RyWDhOUHY1QjN1Z0tXczJ1WW1nYTp3TG9iMkFldW1DZkNMVkpZVnROYjlDSFVYUXNh";

	private AccessTokenClient() {
	}

	public static AccessTokenClient getInstance(){
		try {
			propertiesInputStream = new FileInputStream(propertiesFilePath);
		} catch (FileNotFoundException e) {
			log.error("API specific properties file could not be found at: " + propertiesFilePath);
		}

		//load a properties file from class path, inside static method
		try {
			properties.load(propertiesInputStream);
		} catch (IOException e) {
			log.error("Loading API specific properties from file at - " + propertiesFilePath + " failed...");
		}

		tokenURL = properties.getProperty("tokenURL");
		grantType = properties.getProperty("grantType");
		scope = properties.getProperty("scope");
		appToken = properties.getProperty("appToken");

		try {
			propertiesInputStream.close();
		} catch (IOException e) {
			log.warn("Properties file reading stream could not be closed");
		}

		return accessTokenClient;
	}


	public AccessTokenInfo getAccessToken(String username,String password ,String appInstanceId) throws AccessTokenException {
		SSLContext ctx;
		String response = "";
		try {
			ctx = SSLContext.getInstance("TLS");

			ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()},
					 new SecureRandom());
			SSLContext.setDefault(ctx);

			URL url = new URL(tokenURL);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			});
			//System.out.println(conn.getResponseCode());
			conn.disconnect();

			HttpClient httpClient = new HttpClient();

			PostMethod postMethod = new PostMethod(tokenURL);
			postMethod.addParameter(new NameValuePair("grant_type", grantType));
			postMethod.addParameter(new NameValuePair("username",username));
			postMethod.addParameter(new NameValuePair("password", password));
			postMethod.addParameter(new NameValuePair("scope", scope + appInstanceId));

			postMethod.addRequestHeader("Authorization",
										"Basic " + appToken);
			postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");

			httpClient.executeMethod(postMethod);


			response = postMethod.getResponseBodyAsString();
			log.info(response);
			JSONObject jsonObject=new JSONObject(response);

			AccessTokenInfo accessTokenInfo=new AccessTokenInfo();
			accessTokenInfo.setAccess_token(jsonObject.getString("access_token"));
			accessTokenInfo.setRefresh_token(jsonObject.getString("refresh_token"));
			accessTokenInfo.setExpires_in(jsonObject.getInt("expires_in"));
			accessTokenInfo.setToken_type(jsonObject.getString("token_type"));


			return accessTokenInfo;


		} catch (NoSuchAlgorithmException | KeyManagementException| IOException |JSONException e) {
			log.error(e.getMessage());
			throw new AccessTokenException("Configuration Error for Access Token Generation");
		}catch (NullPointerException e){

			return null;
		}


	}


	private static class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

}



