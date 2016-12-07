package cn.kanmars.kb.server.kernel.manager;

import cn.kanmars.kb.server.kernel.KBKernel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 3/10/16.
 */
public class KBKernelManager {
    private static ConcurrentHashMap<String,KBKernel>  kbKernelMap = new ConcurrentHashMap<String,KBKernel>();
    public synchronized static KBKernel getOrInstanceKBKernel(String serverName){
        KBKernel result = kbKernelMap.get(serverName);
        if(result==null){
            result = new KBKernel();
            kbKernelMap.put(serverName,result);
        }
        return result;
    }
}
