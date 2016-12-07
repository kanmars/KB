package cn.kanmars.kb.client.proxy;

import cn.kanmars.kb.protocol.AbstractKBProtocol;
import cn.kanmars.kb.protocol.manager.KBProtocolManager;

import java.util.List;
import java.util.Map;

/**
 * Created by baolong on 2016/3/8.
 */
public abstract class KBRouteClientProxy {
    protected String register;
    protected String url;
    protected String name;
    protected String group;
    protected String globalname;
    protected String aeskey;
    protected AbstractKBProtocol kbProtocol = KBProtocolManager.getDefaultKBProtocol();
    protected int retryTimes = 0;

    protected List<String> bakUrl;


    public abstract Map exec(Map param);


    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGlobalname() {
        return globalname;
    }

    public void setGlobalname(String globalname) {
        this.globalname = globalname;
    }

    public String getAeskey() {
        return aeskey;
    }

    public void setAeskey(String aeskey) {
        this.aeskey = aeskey;
        this.kbProtocol.setAeskey(aeskey);
    }

    public AbstractKBProtocol getKbProtocol() {
        return kbProtocol;
    }

    public void setKbProtocol(AbstractKBProtocol kbProtocol) {
        this.kbProtocol = kbProtocol;
    }

    public List<String> getBakUrl() {
        return bakUrl;
    }

    public void setBakUrl(List<String> bakUrl) {
        this.bakUrl = bakUrl;
    }
}
