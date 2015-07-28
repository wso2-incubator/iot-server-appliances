package org.wso2.devicemgt.raspberry.agent;

/**
 * Created by ace on 7/25/15.
 */
public class AgentInitializer {
    public static void main(String args[]){
        (new Thread(new SidhdhiQuery())).start();
        (new Thread(new MQTTPolicyReciever())).start();
    }
}
