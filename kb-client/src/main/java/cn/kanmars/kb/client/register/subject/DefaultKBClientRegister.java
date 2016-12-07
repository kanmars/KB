package cn.kanmars.kb.client.register.subject;

import cn.kanmars.kb.client.proxy.KBRouteClientProxy;
import cn.kanmars.kb.client.register.KBClientRegister;
import cn.kanmars.kb.util.DateUtils;
import cn.kanmars.kb.util.LoggerUtil;
import cn.kanmars.kb.util.StringUtils;
import cn.kanmars.kb.util.ZookeeperPathUtil;
import com.sun.corba.se.spi.activation.ServerManager;
import net.sf.json.JSONObject;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.eclipse.jetty.util.ConcurrentHashSet;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by baolong on 2016/3/10.
 */
public class DefaultKBClientRegister implements KBClientRegister {
    //注册地址列表
    private String registerAddress;

    //心跳时间  秒
    private Integer heatbeattime=10;

    //心跳浮动时间秒，用于防止注册拥堵
    private Integer hearbeattimedeviation=10;

    private ConcurrentHashSet<KBRouteClientProxy> kbRouteClientProxySet ;

    private HashSet<ZooKeeper> zkSet = new HashSet<ZooKeeper>();

    private static ClientRegisterThread clientRegisterThread;

    private final static int DEFAULT_ZKTIMEOUT = 30000;

    public void setKBRouteClientProxy(KBRouteClientProxy kbRouteClientProxy) {
        kbRouteClientProxySet.add(kbRouteClientProxy);
    }

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public static class ClientRegisterThread implements Runnable{
        private ClientRegisterThread ClientRegisterThread = this;
        private ConcurrentHashMap<String,ZooKeeper> zookeeperMap = new ConcurrentHashMap<String, ZooKeeper>();
        private ConcurrentHashSet<KBRouteClientProxy> kbRouteClientProxySet = new ConcurrentHashSet<KBRouteClientProxy>();
        public void run() {
            //获取到初始值
                while(true){
                    for (KBRouteClientProxy kbRouteClientProxy : kbRouteClientProxySet) {
                        if(!StringUtils.isEmpty(kbRouteClientProxy.getUrl()))return;
                        ZooKeeper zookeeper = null;
                        try {
                            zookeeper = getRandomZooKeeper(kbRouteClientProxy);
                            final KBRouteClientProxy kbRouteClientProxy_final = kbRouteClientProxy;
                            String globalName = kbRouteClientProxy.getGlobalname();
                            String group = kbRouteClientProxy.getGroup();
                            String path = ZookeeperPathUtil.createZKServerListPath(globalName,group);
                            mkZkDirs(zookeeper, path);
                            List<String> ip_port_list = zookeeper.getChildren(path, new Watcher() {
                                public void process(WatchedEvent watchedEvent) {
                                    //通知该地址上的client
                                    ClientRegisterThread.notify();
                                }
                            });
                            List<String> urlList = getANewServerURl(ip_port_list, zookeeper, path);
                            if(urlList.size()==0){
                                throw new RuntimeException("未找到服务端");
                            }
                            String url = urlList.get(0);
                            urlList.remove(0);
                            kbRouteClientProxy_final.setUrl("http://"+url);
                            kbRouteClientProxy_final.setBakUrl(urlList);
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            try {
                                zookeeper.close();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }


        public static ZooKeeper getRandomZooKeeper(KBRouteClientProxy kbRouteClientProxy){

            //获取到初始值
            if(StringUtils.isEmpty(kbRouteClientProxy.getRegister())){
                throw new RuntimeException("注册地址为空");
            }
            String[] registerAddresArray = kbRouteClientProxy.getRegister().split(",");
            int i = new Random().nextInt(registerAddresArray.length);
            ZooKeeper zookeeper = null;
            while(true) {
                //第一层循环，用于循环注册地址
                if (i == registerAddresArray.length) {
                    //已到达最后一个，则从0开始
                    i = 0;
                }
                try {
                    LoggerUtil.print(LoggerUtil.DEBUG, "创建了zookeeper链接",null);
                    zookeeper = new ZooKeeper(registerAddresArray[i], DEFAULT_ZKTIMEOUT, new Watcher() {
                        public void process(WatchedEvent watchedEvent) {
                            //已经触发了 [" + watchedEvent.getType() + "] 事件
                        }
                    });
                    LoggerUtil.print(LoggerUtil.DEBUG, "创建了zookeeper链接成功",null);
                    Stat stat =zookeeper.exists("/",false);
                    break;
                } catch (Exception e) {
                    LoggerUtil.print(LoggerUtil.DEBUG, "创建了zookeeper链接创建失败",e);
                }
            }

            return zookeeper;
        }
        public void closeZooKeeper(ZooKeeper zooKeeper){
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public static List<String> getANewServerURl(List<String> childList,ZooKeeper zookeeper,String basePath){
            if(childList.size()==0)return new ArrayList<String>();
            List<String> result = null;
            int begin = new Random().nextInt(childList.size());
            while(true){
                try {
                    result = new ArrayList<String>();
                    for(int i = begin;i<childList.size();i++){
                        result.add(childList.get(i));
                    }
                    for(int i=0;i<begin;i++){
                        result.add(childList.get(i));
                    }
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        public void addKBRouteClientProxy(KBRouteClientProxy proxy){
            this.kbRouteClientProxySet.add(proxy);
        }
    }

    public static void main(String[] args) throws Exception {
        //asyn();
        final ZooKeeper zookeeper = new ZooKeeper("10.126.53.196:32184", DEFAULT_ZKTIMEOUT, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                //System. out .println( " 已经触发了 [" + watchedEvent.getType() + "] 事件 " );
            }
        });
        mkZkDirs(zookeeper, "/kb/a/b/c/d/e/f");



        zookeeper.close();
    }

    public static void mkZkDirs(ZooKeeper zookeeper,String path){
        String[] pathArray = path.split("/");
        String currentPath = "";
        L:for(String pathNode : pathArray){
            if(StringUtils.isEmpty(pathNode))continue ;
            currentPath +="/"+pathNode;
            try {
                int count=0;
                while(true){
                    Stat stat = zookeeper.exists(currentPath,false);
                    if(stat==null){
                        zookeeper.create(currentPath,"OK".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE , CreateMode.PERSISTENT);
                    }else{
                        break;//直到创建成功为止
                    }
                    count++;
                    if(count >=500){
                        throw new Exception("创建路径发生异常["+path+"]");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
