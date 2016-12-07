package cn.kanmars.kb.client.register.manager;

import cn.kanmars.kb.client.register.subject.DefaultKBClientRegister;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by baolong on 2016/3/10.
 */
public class KBClientRegisterManager {
    private static DefaultKBClientRegister register = new DefaultKBClientRegister();
    private static DefaultKBClientRegister.ClientRegisterThread registerThread = new DefaultKBClientRegister.ClientRegisterThread();
    private static ExecutorService executorService =  Executors.newFixedThreadPool(3);
    public static synchronized DefaultKBClientRegister getDefaultKBClientRegister(){
        return register;
    }
    public static synchronized DefaultKBClientRegister.ClientRegisterThread getClientRegisterThread(){
        return registerThread;
    }

    public static synchronized void start(){
        executorService.execute(registerThread);
    }
    public static synchronized void stop(){
        executorService.shutdown();
    }

}
