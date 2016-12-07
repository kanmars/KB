package cn.kanmars.kb.server.server;

import cn.kanmars.kb.server.conf.KBServerConfig;
import cn.kanmars.kb.server.kernel.KBKernel;

/**
 * Created by kanmars2009 on 2016/3/7.
 */
public interface KBServer {
    /**
     * 获取配置信息
     */
    public KBServerConfig getKbServerConfig();

    /**
     * 获取内核信息
     * @return
     */
    public KBKernel getKbKernel();

    /**
     * 初始化服务
     * @param kbServerConfig
     */
    public void init(KBServerConfig kbServerConfig) throws Exception ;

    /**
     * 启动服务
     * @param kbServerConfig
     */
    public void start(KBServerConfig kbServerConfig) throws Exception;

    /**
     * 关闭服务
     * @param kbServerConfig
     */
    public void stop(KBServerConfig kbServerConfig) throws Exception ;
    /**
     * 等待服务停止
     */
    public void join() throws Exception ;
}
