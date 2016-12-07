package cn.kanmars.service;

import cn.kanmars.kb.server.annotation.KBRouteServer;
import cn.kanmars.kb.util.LoggerUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by baolong on 2016/3/7.
 */
@Service
public class CCC {

    @KBRouteServer(group = "KB_DEFAULT",globalname = "cn.kanmars.service.CCC.exec0",servername="kbserver1",aeskey = "getAesKeyFromSpring")
    public Map exec0(Map reqInfo){
        String s = "cn.kanmars.service.CCC.exec0";
        LoggerUtil.print(LoggerUtil.INFO, "调用了服务[" + s + "]", null);
        return null;
    }
    @KBRouteServer(group = "KB_DEFAULT",globalname = "cn.kanmars.service.CCC.exec1",servername="kbserver1",aeskey = "getAesKeyFromSpring")
    public Map exec1(Map reqInfo){
        String s = "cn.kanmars.service.CCC.exec1";
        LoggerUtil.print(LoggerUtil.INFO, "调用了服务[" + s + "]", null);
        return null;
    }
    @KBRouteServer(group = "KB_DEFAULT",globalname = "cn.kanmars.service.CCC.exec0",servername="kbserver2",aeskey = "getAesKeyFromSpring")
    public Map exec2(Map reqInfo){
        String s = "cn.kanmars.service.CCC.exec0";
        LoggerUtil.print(LoggerUtil.INFO, "调用了服务[" + s + "]", null);
        return null;
    }
    @KBRouteServer(group = "KB_DEFAULT",globalname = "cn.kanmars.service.CCC.exec1",servername="kbserver2",aeskey = "getAesKeyFromSpring")
    public Map exec3(Map reqInfo){
        String s = "cn.kanmars.service.CCC.exec1";
        LoggerUtil.print(LoggerUtil.INFO, "调用了服务[" + s + "]", null);
        return null;
    }
}
