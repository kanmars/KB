package cn.kanmars.kb.client.client.manager;

import cn.kanmars.kb.client.client.KBClient;
import cn.kanmars.kb.client.client.subject.JettyKBClient;
import org.eclipse.jetty.client.HttpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baolong on 2016/3/8.
 */
public class ClientManager {

    public static ClientManager clientManager = new ClientManager();
    public synchronized static ClientManager getInstance(){
        return clientManager;
    }

    public static KBClient kbClient;

    public static synchronized KBClient getClient(){
        if(kbClient == null){
            kbClient = new JettyKBClient();
            try {
                kbClient.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return kbClient;
    }
    public static synchronized void shutdown(){
        try {
            getClient().stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
