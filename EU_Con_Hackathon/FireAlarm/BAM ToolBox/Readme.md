## WSO2 BAM Integration

This page provides the necessary utilities to setup WSO2 BAM to generate statistics from the sensor-data published to its datastore via the firealarm/sensebot.

Get a broader understanding of configuring WSO2-BAM to publish data by reading the documentation in the following link:

		- This link provides the configurations for WSO2-APIM with WSO2-BAM to receive and view APIM data
			https://docs.wso2.com/display/AM180/Publishing+API+Runtime+Statistics


### How To

* Add a new datasource by the name **WSO2IOT_STATS_DB** to the *bam-datasources.xml* of WSO2-BAM found in **{WSO2BAM_HOME}/repository/conf/datasources/bam-datasources.xml**
	- You can copy paste the **WSO2IOT_STATS_DB** datasource definition given below. 

```xml
 <datasource>
      <name>WSO2IOT_STATS_DB</name>
      <jndiConfig>
          <name>jdbc/WSO2IOT_STATS_DB</name>
      </jndiConfig>
      <description>The datasource used for analyzer data</description>
      <definition type="RDBMS">
          <configuration>
              <url>jdbc:h2:repository/database/IOT_STATS_DB;AUTO_SERVER=TRUE</url>
              <username>wso2carbon</username>
              <password>wso2carbon</password>
              <driverClassName>org.h2.Driver</driverClassName>
              <maxActive>50</maxActive>
              <maxWait>60000</maxWait>
              <testOnBorrow>true</testOnBorrow>
              <validationQuery>SELECT 1</validationQuery>
              <validationInterval>30000</validationInterval>
          </configuration>
      </definition>
  </datasource>
```
* Install the toolbox **IOT_Analytics.tbox** from the WSO2-BAM Management Console UI. If not the dashboard will not appear.

<!---
* ***[For testing purposes where there are no real sensor data published to BAM yet]***
	- Extract attached Client **WSO2DevicePlatform-2.0.0.zip** 
	- execute "java -jar -Dmachine.ip=172.16.3.212 target/WSO2DevicePlatform-1.0.0-jar-with-dependencies.jar" in side /device-cloud-appliances/WSO2DevicePlatform-1.0.0

* Go to the portal section under gadgets to view the dashboard.
-->
