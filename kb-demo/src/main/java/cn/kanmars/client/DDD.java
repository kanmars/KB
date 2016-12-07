package cn.kanmars.client;

import cn.kanmars.kb.client.annotation.KBRouteClient;
import cn.kanmars.kb.client.proxy.KBRouteClientProxy;
import cn.kanmars.kb.util.LoggerUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by baolong on 2016/3/8.
 */
@Service
public class DDD {

    //@KBRouteClient(register = "10.126.53.196:32184",url = "http://localhost:9999",name="DDD_KB_0308",group = "KB_DEFAULT",globalname = "cn.kanmars.service.CCC.exec0",aeskey="getAesKeyFromSpring")
    @KBRouteClient(register = "10.126.53.196:32184",url = "",name="DDD_KB_0308",group = "KB_DEFAULT",globalname = "cn.kanmars.service.CCC.exec0",aeskey="getAesKeyFromSpring")
    public KBRouteClientProxy kbRouteClientProxy;

    public void exec() {
        long pre =System.currentTimeMillis();
        long now =System.currentTimeMillis();
        LoggerUtil.print(LoggerUtil.INFO, "初始时间["+(now-pre)+"]", null);
        HashMap param = new HashMap();
        Map result = null;
        for(int i=0;i<10000;i++){
            try{
                //Thread.sleep(2000);
                result = kbRouteClientProxy.exec(param);
                //-----------------------------------
                now =System.currentTimeMillis();
                LoggerUtil.print(LoggerUtil.INFO, "第["+i+"]次调用耗时[" + (now - pre) + "]", null);
                pre =System.currentTimeMillis();
            }catch (Exception e){
                e.printStackTrace();
            }


        }


    }
}
