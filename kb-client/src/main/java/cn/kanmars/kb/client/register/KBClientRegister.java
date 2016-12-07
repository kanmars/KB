package cn.kanmars.kb.client.register;

import cn.kanmars.kb.client.proxy.KBRouteClientProxy;

/**
 * Created by baolong on 2016/3/10.
 */
public interface KBClientRegister {

    public void setKBRouteClientProxy(KBRouteClientProxy kbRouteClientProxy);

    public String getRegisterAddress();

    public void setRegisterAddress(String registerAddress);
}
