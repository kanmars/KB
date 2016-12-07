package cn.kanmars.kb.server.register;

import cn.kanmars.kb.server.kernel.KBKernel;

/**
 * Created by baolong on 2016/3/8.
 */
public interface KBServerRegister {

    public void register(String address,KBKernel kernel);

    public String getRegisterAddress();

    public void setRegisterAddress(String registerAddress);

}
