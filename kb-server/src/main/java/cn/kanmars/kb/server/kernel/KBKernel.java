package cn.kanmars.kb.server.kernel;

import cn.kanmars.kb.server.dispatcher.KBDisPatcher;
import cn.kanmars.kb.server.server.KBServer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by baolong on 2016/3/7.
 */
public class KBKernel {
    private KBServer kbServer = null;

    private ConcurrentHashMap<String,KBDisPatcher> kbKernel = new ConcurrentHashMap<String,KBDisPatcher>();

    public synchronized void register(String path,KBDisPatcher kbDisPatcher){
        kbKernel.put(path,kbDisPatcher);
    }

    public synchronized void unregister(String path,KBDisPatcher kbDisPatcher){
        kbKernel.remove(path);
    }

    public synchronized void clean(String path,KBDisPatcher kbDisPatcher){
        kbKernel.clear();
    }

    public synchronized KBServer getKbServer() {
        return kbServer;
    }

    public KBDisPatcher getKBDispatcher(String path){
        return kbKernel.get(path);
    }

    public synchronized void setKbServer(KBServer kbServer) {
        this.kbServer = kbServer;
    }

    public synchronized ConcurrentHashMap<String,KBDisPatcher> getKbKernel(){
        return kbKernel;
    }
}
