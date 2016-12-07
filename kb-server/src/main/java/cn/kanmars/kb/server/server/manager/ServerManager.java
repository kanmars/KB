package cn.kanmars.kb.server.server.manager;

import cn.kanmars.kb.server.conf.KBServerConfig;
import cn.kanmars.kb.server.server.KBServer;
import cn.kanmars.kb.server.server.subject.JettyKBServer;
import org.eclipse.jetty.util.ConcurrentHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kanmars2009 on 2016/3/7.
 */
public class ServerManager {
    public static ServerManager serverManager = new ServerManager();
    public synchronized static ServerManager getInstance(){
        return serverManager;
    }

    private ConcurrentHashMap<String,KBServer> serverMap = new ConcurrentHashMap<String,KBServer>();

    public KBServer kbserver = null;

    public synchronized KBServer createNewServer(){
        return new JettyKBServer();
    }

    public synchronized void registerServer(String servername,KBServer kbServer){
        serverMap.put(servername,kbServer);
    }

    public void join() throws Exception {
        for(KBServer kbserver: serverMap.values()){
            kbserver.join();
        }
    }

    public void startAll(KBServerConfig kbServerConfig) throws Exception {
        for(KBServer kbserver: serverMap.values()){
            kbserver.start(kbServerConfig);
        }
    }

    public void stopAll(KBServerConfig kbServerConfig) throws Exception {
        for(KBServer kbserver: serverMap.values()){
            kbserver.stop(kbServerConfig);
        }
    }

    public synchronized KBServer getServer(String servername){
        return serverMap.get(servername);
    }

    public static ConcurrentHashSet<String> kbRouteChecker = new ConcurrentHashSet<String>();

    public static synchronized void addKbRouteServer(String group,String globalname,String servername){
        if(group==null || globalname==null || servername==null){
            throw new RuntimeException("group、globalname、servername为空");
        }
        String routeServerSign = group.trim()+"_"+globalname.trim()+"_"+servername.trim();
        if(kbRouteChecker.contains(routeServerSign)){
            throw new RuntimeException("已经存在了相应的KBRouteServer["+routeServerSign+"]");
        }
    }

}
