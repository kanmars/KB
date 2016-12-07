package cn.kanmars.kb.server.conf;

import cn.kanmars.kb.server.beanpostProcessor.KBRouteServerBeanPostProcessor;
import cn.kanmars.kb.server.register.subject.DefaultKBServerRegister;
import cn.kanmars.kb.server.server.KBServer;
import cn.kanmars.kb.server.server.manager.ServerManager;
import cn.kanmars.kb.config.AbstractKBConfig;
import cn.kanmars.kb.config.inter.Configer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Created by kanmars2009 on 2016/3/6.
 */
public class KBServerConfig extends AbstractKBConfig implements Configer,InitializingBean,DisposableBean,ApplicationContextAware,BeanNameAware {

    private ApplicationContext applicationContext;

    private String bindip;
    private Integer bingport;
    private Integer minthreadpool;
    private Integer maxthreadpool;
    private Integer idleTimeout;
    private String register;

    private String annotation="cn.kanmars.kb.server.annotation.KBRouteServer";

    public void init(){
    }

    public void check() throws Exception {
        if(bindip==null){
            throw new Exception("bindip 不可以为空");
        }
        if(bingport==null){
            throw new Exception("bingport 不可以为空");
        }
        if(minthreadpool==null){
            throw new Exception("minthreadpool 不可以为空");
        }
        if(maxthreadpool==null){
            throw new Exception("maxthreadpool 不可以为空");
        }
        if(register==null){
            throw new Exception("register 不可以为空");
        }
    }

    public void start() throws Exception {

    }

    public void stop() throws Exception {

    }


    public String getBindip() {
        return bindip;
    }

    public Integer getBingport() {
        return bingport;
    }

    public Integer getMinthreadpool() {
        return minthreadpool;
    }

    public Integer getMaxthreadpool() {
        return maxthreadpool;
    }

    public Integer getIdleTimeout() {
        return idleTimeout;
    }

    public String getRegister() {
        return register;
    }

    public void setBindip(String bindip) {
        this.bindip = bindip;
    }

    public void setBingport(Integer bingport) {
        this.bingport = bingport;
    }

    public void setMinthreadpool(Integer minthreadpool) {
        this.minthreadpool = minthreadpool;
    }

    public void setMaxthreadpool(Integer maxthreadpool) {
        this.maxthreadpool = maxthreadpool;
    }

    public void setIdleTimeout(Integer idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public void afterPropertiesSet() throws Exception {
        //启动服务器
        KBServer kbServer = ServerManager.getInstance().createNewServer();
        ServerManager.getInstance().registerServer(this.beanName,kbServer);
        ServerManager.getInstance().getServer(this.beanName).init(this);
        ServerManager.getInstance().getServer(this.beanName).start(this);

        //启动扫描器
        String name = "KBRouteServerBeanPostProcessor";
        if (!applicationContext.containsBean(name)) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(KBRouteServerBeanPostProcessor.class);
            registerBean(name, beanDefinitionBuilder.getRawBeanDefinition());
        }
        KBRouteServerBeanPostProcessor kbRouteServerBeanPostProcessor = (KBRouteServerBeanPostProcessor)applicationContext.getBean(name);
        ((AbstractApplicationContext)applicationContext).getBeanFactory().addBeanPostProcessor(kbRouteServerBeanPostProcessor);

        String[] singletomNames = ((AbstractApplicationContext)applicationContext).getBeanFactory().getSingletonNames();
        for(String singletomName : singletomNames){
            Object bean = applicationContext.getBean(singletomName);
            bean = kbRouteServerBeanPostProcessor.postProcessBeforeInitialization(bean,singletomName);
            bean = kbRouteServerBeanPostProcessor.postProcessAfterInitialization(bean, singletomName);
        }
    }

    public void destroy() throws Exception {
        ServerManager.getInstance().startAll(this);
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    /**
     * @desc 向spring容器注册bean
     * @param beanName
     * @param beanDefinition
     */
    private void registerBean(String beanName, BeanDefinition beanDefinition) {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        BeanDefinitionRegistry beanDefinitonRegistry = (BeanDefinitionRegistry) configurableApplicationContext.getBeanFactory();
        beanDefinitonRegistry.registerBeanDefinition(beanName, beanDefinition);
    }

    private String beanName;
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
    public String getBeanName(){return beanName;}
}
