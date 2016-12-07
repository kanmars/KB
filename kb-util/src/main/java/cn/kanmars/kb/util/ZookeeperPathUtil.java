package cn.kanmars.kb.util;

/**
 * Created by baolong on 2016/3/10.
 */
public class ZookeeperPathUtil {
    public static String createZKServerListPath(String globalname,String group){
        String path = "/kb/" + globalname + "/" + group + "/serverlist";
        return path;
    }
    public static String createZKClientListPath(String globalname,String group){
        String path = "/kb/" + globalname + "/" + group + "/clientlist";
        return path;
    }
}
