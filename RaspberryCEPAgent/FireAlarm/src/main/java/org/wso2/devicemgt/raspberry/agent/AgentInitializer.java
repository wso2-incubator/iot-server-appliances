package org.wso2.devicemgt.raspberry.agent;

/**
 * Created by ace on 7/25/15.
 */
public class AgentInitializer {
    private static Boolean policyUpdated = false;
    private static final Object lock = new Object();

    public static void main(String args[]){
        (new Thread(new SidhdhiQuery())).start();
        (new Thread(new MQTTPolicyReciever())).start();
    }

    public static void setUpdated(Boolean x) {
        synchronized (lock) {
            policyUpdated = x;
        }
    }

    public static Boolean isUpdated() {
        synchronized (lock) {
            Boolean temp = policyUpdated;
            policyUpdated = false;
            return temp;
        }
    }
}
