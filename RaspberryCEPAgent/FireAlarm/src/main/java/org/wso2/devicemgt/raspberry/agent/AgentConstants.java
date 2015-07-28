package org.wso2.devicemgt.raspberry.agent;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by ace on 7/25/15.
 */
public class AgentConstants {

    public static Properties prop = new Properties();

    AgentConstants(){
        InputStream input = null;

        try {

            input = new FileInputStream("agent.properties");
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
