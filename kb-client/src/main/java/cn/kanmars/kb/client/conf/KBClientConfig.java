package cn.kanmars.kb.client.conf;

import cn.kanmars.kb.client.beanpostProcessor.KBRouteClientBeanPostProcessor;
import cn.kanmars.kb.client.register.manager.KBClientRegisterManager;
import cn.kanmars.kb.client.register.subject.DefaultKBClientRegister;
import cn.kanmars.kb.config.AbstractKBConfig;
import cn.kanmars.kb.config.inter.Configer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.Map;
import java.util.Set;

/**
 * Created by kanmars2009 on 2016/3/6.
 */
public class KBClientConfig extends AbstractKBConfig implements Configer,InitializingBean,DisposableBean,ApplicationContextAware {

    ApplicationContext applicationContext;

    private String basepackage;

    private String annotation="cn.kanmars.kb.client.annotation.KBRouteServer";

    public void init() throws Exception {

    }

    public void check() throws Exception {
        if(basepackage==null){
            throw new Exception("basepackage 不可以为空");
        }
    }

    public String getBasepackage() {
        return basepackage;
    }

    public void setBasepackage(String basepackage) {
        this.basepackage = basepackage;
    }

    public void start() throws Exception {

    }

    public void stop() throws Exception {

    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void destroy() throws Exception {

    }

    public void afterPropertiesSet() throws Exception {
        //在application中注册BeanPostProcessor
        String name = "KBRouteClientBeanPostProcessor";
        if (!applicationContext.containsBean(name)) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(KBRouteClientBeanPostProcessor.class);
            registerBean(name, beanDefinitionBuilder.getRawBeanDefinition());
        }
        KBRouteClientBeanPostProcessor kbRouteClientBeanPostProcessor = (KBRouteClientBeanPostProcessor)applicationContext.getBean(name);
        ((AbstractApplicationContext)applicationContext).getBeanFactory().addBeanPostProcessor(kbRouteClientBeanPostProcessor);

        String[] singletomNames = ((AbstractApplicationContext)applicationContext).getBeanFactory().getSingletonNames();
        for(String singletomName : singletomNames){
            Object bean = applicationContext.getBean(singletomName);
            bean = kbRouteClientBeanPostProcessor.postProcessBeforeInitialization(bean,singletomName);
            bean = kbRouteClientBeanPostProcessor.postProcessAfterInitialization(bean, singletomName);
        }

        DefaultKBClientRegister.ClientRegisterThread ClientRegisterThread = new DefaultKBClientRegister.ClientRegisterThread();
        KBClientRegisterManager.start();

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
}
