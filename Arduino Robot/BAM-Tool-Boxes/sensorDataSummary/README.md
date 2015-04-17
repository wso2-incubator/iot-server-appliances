## WSO2 BAM Integration

This page provides the necessary utilities to setup WSO2 BAM to generate statistics from the sensor-data published to its datastore via the python client.

Get a broader understanding of configuring WSO2-BAM to publish data by reading the documentation in the following link:

		- This link provides the configurations for WSO2-APIM with WSO2-BAM to receive and view APIM data
			https://docs.wso2.com/display/AM180/Publishing+API+Runtime+Statistics


### How To

* Add a new datasource by the name **WSO2_IOT_DB** to the "master-datasources.xml" of WSO2-BAM found in {WSO2BAM_HOME}/repository/conf/datasources/

* Install the toolbox "IoT_Devices_Analytics.tbox" from the WSO2-BAM Management Console UI. If not the dashboard will not appear.

* [For testing purposes where there are no real sensor data published to BAM yet]
	-	Open the attached Client WSO2DevicePlatform-2.0.0.zip with IDE and run it, to propergate the sample data.

* Go to the portal section under gadgets to view the dashboard.
