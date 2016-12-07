package cn.kanmars.kb.server.register.subject;

import cn.kanmars.kb.server.dispatcher.KBDisPatcher;
import cn.kanmars.kb.server.kernel.KBKernel;
import cn.kanmars.kb.server.register.KBServerRegister;
import cn.kanmars.kb.server.server.KBServer;
import cn.kanmars.kb.server.server.manager.ServerManager;
import cn.kanmars.kb.util.DateUtils;
import cn.kanmars.kb.util.LoggerUtil;
import cn.kanmars.kb.util.StringUtils;
import cn.kanmars.kb.util.ZookeeperPathUtil;
import net.sf.json.JSONObject;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.awt.image.Kernel;
import java.io.IOException;
import java.util.*;

/**
 * Created by baolong on 2016/3/8.
 */
public class DefaultKBServerRegister implements KBServerRegister {

    //注册地址列表
    private String registerAddress;

    //心跳时间  秒
    private Integer heatbeattime=10;

    //心跳浮动时间秒，用于防止注册拥堵
    private Integer hearbeattimedeviation=10;

    private HashSet<ZooKeeper> zkSet = new HashSet<ZooKeeper>();

    private final static int DEFAULT_ZKTIMEOUT = 30000;

    /**
     * 注册任务
     * 1、按照心跳时间向服务器推送心跳，并原子性更新服务器的最后记录时间
     * Diamond的数据结构为JSON字符串
     * group为group
     * dataId为servername
     * content为JSON结构
     *  serverAddrArrar:{
     *      "IP:PORT":{lastheartbeat,user.......}
     *
     *  }
     * @param address
     * @param kernel
     */
    public void register(String address, KBKernel kernel) {
        if(StringUtils.isEmpty(registerAddress)){
            throw new RuntimeException("发生运行时异常registerAddress为空");
        }
        //解析注册地址，随机挑其中一个为注册中心
        String[] registeraddr = registerAddress.split(",");
        int i = new Random().nextInt(registeraddr.length);
        String ip_port = registeraddr[i];
        try {
            ZooKeeper zk = new ZooKeeper(ip_port, DEFAULT_ZKTIMEOUT, new Watcher() {
                public void process(WatchedEvent watchedEvent) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public static class ServerRegisterThread implements Runnable{
        private String registerAddress;
        private ZooKeeper zookeeper = null;
        private KBKernel kbKernel = null;
        public void run() {
            //获取到初始值
            if(StringUtils.isEmpty(registerAddress)){
                throw new RuntimeException("注册地址为空");
            }
            String[] registerAddresArray = registerAddress.split(",");
            int i = new Random().nextInt(registerAddresArray.length);
            while(true){
                try {
                    zookeeper = getRandomZooKeeper();
                    for(Map.Entry<String,KBDisPatcher> e:kbKernel.getKbKernel().entrySet()){
                        String globalname = (String)e.getValue().getAnnotationMap().get("globalname");
                        String group = (String)e.getValue().getAnnotationMap().get("group");
                        String servername = (String)e.getValue().getAnnotationMap().get("servername");
                        String path = ZookeeperPathUtil.createZKServerListPath(globalname,group);
                        //确保永久文件夹存在
                        mkZkDirs(zookeeper,path);
                        //创建临时文件并建立监听

                        KBServer kbServer = ServerManager.getInstance().getServer(servername);
                        String serverPath = path+"/"+kbServer.getKbServerConfig().getBindip()+":"+kbServer.getKbServerConfig().getBingport();
                        int count_of_update = 0;
                        while(true) {
                            try{
                                Stat stat = zookeeper.exists(serverPath, false);
                                if (stat == null) {
                                    JSONObject json = new JSONObject();
                                    json.put("createTime", DateUtils.getCurrDateTime());
                                    json.put("lastUpdTime", DateUtils.getCurrDateTime());
                                    json.put("weight", 100);
                                    zookeeper.create(serverPath, json.toString().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);//临时节点
                                    break;
                                } else {
                                    byte[] jsonData_ = zookeeper.getData(serverPath, false, stat);
                                    JSONObject json = JSONObject.fromObject(new String(jsonData_, "UTF-8"));
                                    json.put("lastUpdTime", DateUtils.getCurrDateTime());
                                    zookeeper.create(serverPath, json.toString().getBytes("UTF-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);//临时节点
                                    Stat stat_update = zookeeper.setData(serverPath, json.toString().getBytes("UTF-8"), stat.getVersion());
                                    if(stat_update!=null){
                                        break;
                                    }
                                }
                            }catch (Exception ee){
                                LoggerUtil.print(LoggerUtil.DEBUG, "连接时发生异常，尝试第["+(count_of_update+1)+"]次", null);
                            }
                            count_of_update++;
                            if(count_of_update>=10){
                                throw new RuntimeException("发生运行时异常，在指定次数之后任然未更新成功zookeeper");
                            }
                        }
                        //注册成功
                    }
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    closeZooKeeper(zookeeper);
                }

            }

        }
        public ZooKeeper getRandomZooKeeper(){

            //获取到初始值
            if(StringUtils.isEmpty(registerAddress)){
                throw new RuntimeException("注册地址为空");
            }
            String[] registerAddresArray = registerAddress.split(",");
            int i = new Random().nextInt(registerAddresArray.length);
            ZooKeeper zookeeper = null;
            while(true) {
                //第一层循环，用于循环注册地址
                if (i == registerAddresArray.length) {
                    //已到达最后一个，则从0开始
                    i = 0;
                }
                try {
                    zookeeper = new ZooKeeper(registerAddresArray[i], DEFAULT_ZKTIMEOUT, new Watcher() {
                        public void process(WatchedEvent watchedEvent) {
                            // 已经触发了 [" + watchedEvent.getType() + "] 事件 ");
                        }
                    });
                    Stat stat =zookeeper.exists("/",false);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
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

        public String getRegisterAddress() {
            return registerAddress;
        }

        public void setRegisterAddress(String registerAddress) {
            this.registerAddress = registerAddress;
        }

        public ZooKeeper getZookeeper() {
            return zookeeper;
        }

        public void setZookeeper(ZooKeeper zookeeper) {
            this.zookeeper = zookeeper;
        }

        public KBKernel getKbKernel() {
            return kbKernel;
        }

        public void setKbKernel(KBKernel kbKernel) {
            this.kbKernel = kbKernel;
        }
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
                //e.printStackTrace();
            }
        }
    }
}
