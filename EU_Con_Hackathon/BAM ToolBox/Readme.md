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

------------------

#### View summarized data

* The data written to the Cassandra DB in WSO2-BAM is summarized via the hive script found inside the **IOT_Analytics.tbox**. This summarized information is then written to an H2 database. It can be viewed as follows:
	* enable H2 database configuration in the **carbon.xml** file found inside ***<WSO2-BAM-HOME>/repository/conf/.*** folder by uncommenting the section shown below.
	* access the db console via **localhost:8082** in your browser. 
	* give the **url**, **username** and **password** for login according to the datasource definition above.
	
```xml
<H2DatabaseConfiguration>
	<property name="web" />
	<property name="webPort">8082</property>
	<property name="webAllowOthers" />
<!-- commented part 
	<property name="webSSL" />
	<property name="tcp" />
	<property name="tcpPort">9092</property>
	<property name="tcpAllowOthers" />
	<property name="tcpSSL" />
	<property name="pg" />
	<property name="pgPort">5435</property>
	<property name="pgAllowOthers" />
	<property name="trace" />
	<property name="baseDir">${carbon.home}</property> 
		comment ends here	-->
</H2DatabaseConfiguration>
```
