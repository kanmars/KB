package cn.kanmars.kb.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kanmars2009 on 2016/3/6.
 */
public class AbstractKBConfig {
    private Map<String,Object> kbconfig = new HashMap<String, Object>();

    public String getString(String configName){
        Object result=null;
        if(kbconfig.get(configName)!=null){
            return result.toString();
        }
        return null;
    }

    public Integer getInteger(String configName){
        Object result=null;
        if(kbconfig.get(configName)!=null){
            return Integer.parseInt(result.toString());
        }
        return null;
    }
    public Long getLong(String configName){
        Object result=null;
        if(kbconfig.get(configName)!=null){
            return Long.parseLong(result.toString());
        }
        return null;
    }
    public Double getDouble(String configName){
        Object result=null;
        if(kbconfig.get(configName)!=null){
            return Double.parseDouble(result.toString());
        }
        return null;
    }

    public void setKBConfigInfo(String configName,Object configValue){
        kbconfig.put(configName,configValue);
    }

    public Object getKBConfigInfo(String configName){
        return kbconfig.get(configName);
    }
}
