package cn.kanmars.kb.client.client;

/**
 * Created by baolong on 2016/3/8.
 */
public abstract class KBClient {
    public abstract void start() throws Exception;
    public abstract void stop() throws Exception;
    public abstract Object send(String url,String content) throws Exception;
}
