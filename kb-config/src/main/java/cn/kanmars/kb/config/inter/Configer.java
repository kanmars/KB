package cn.kanmars.kb.config.inter;

/**
 * Created by kanmars2009 on 2016/3/6.
 */
public interface Configer {
    /**
     * 配置初始化接口
     */
    public void init() throws Exception;

    /**
     * 配置校验接口
     */
    public void check() throws Exception;

    /**
     * 配置对象启动接口
     */
    public void start() throws Exception;

    /**
     * 配置对象关闭接口
     */
    public void stop() throws Exception;
}
