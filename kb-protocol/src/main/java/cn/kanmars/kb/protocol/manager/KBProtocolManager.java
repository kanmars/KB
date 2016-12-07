package cn.kanmars.kb.protocol.manager;

import cn.kanmars.kb.protocol.AbstractKBProtocol;
import cn.kanmars.kb.protocol.subject.DefaultRestfulKBProtocol;

/**
 * Created by baolong on 2016/3/7.
 */
public class KBProtocolManager {

    public static AbstractKBProtocol kbProtocol;

    public static AbstractKBProtocol getDefaultKBProtocol(){
        if(kbProtocol==null){
            kbProtocol = new DefaultRestfulKBProtocol();
        }
        return kbProtocol;
    }
}
