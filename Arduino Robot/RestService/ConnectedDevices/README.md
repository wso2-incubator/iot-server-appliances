### Connected Devices
-----------------

This is JAX-RS project with a service endpoint to be called by any external client to publish data to WSO2-BAM. In this case the service is called by the python client provided at:

**/Arduino Robot/PC_Clients/PythonRobotController/RESTPublishClient/RESTClient.py**

Simply download the project, build it and deploy the generated *.war* file in a running instance of the WSO2-AS and make the appropiate configurations in the python client.

1. *Download the project folder*

2. *Navigate into it via the terminal and do a "mvn install"*

3. *Deploy the generated .war file into WSO2-AS* 

		- (WSO2 AS: https://docs.wso2.com/display/AS520/Deploying+JAX-WS+and+JAX-RS+Applications)

4. *Configure the python client with the appropiate network parameters* 



