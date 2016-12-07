package cn.kanmars.kb.client.beanpostProcessor;

import cn.kanmars.kb.client.annotation.KBRouteClient;
import cn.kanmars.kb.client.proxy.KBRouteClientProxy;
import cn.kanmars.kb.client.proxy.subject.DefaultKBRouteClientProxy;
import cn.kanmars.kb.client.register.manager.KBClientRegisterManager;
import cn.kanmars.kb.util.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Created by baolong on 2016/3/8.
 */
public class KBRouteClientBeanPostProcessor implements BeanPostProcessor,ApplicationContextAware {

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class clazz = bean.getClass();
        Field[] fields = clazz.getFields();
        for(Field field:fields){
            //如果从这个field上发现了KBRouteClient的注解
            KBRouteClient kbRouteClient = field.getAnnotation(KBRouteClient.class);
            if(kbRouteClient!=null){
                String register = kbRouteClient.register();
                String url = kbRouteClient.url();
                String name = kbRouteClient.name();
                if(StringUtils.isEmpty(name)){
                    name = "KBRouteClient_"+ UUID.randomUUID().toString();
                }
                String group = kbRouteClient.group();
                String globalname = kbRouteClient.globalname();
                String aeskey = kbRouteClient.aeskey();
                //判断该name的bean在上下文中是否存在
                KBRouteClientProxy proxy = null;
                try{
                    proxy = (KBRouteClientProxy)applicationContext.getBean(name);
                }catch (Exception e){
                    //e.printStackTrace();
                }
                if( proxy == null ){
                    BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DefaultKBRouteClientProxy.class);
                    registerBean(name, beanDefinitionBuilder.getRawBeanDefinition());
                    proxy = (KBRouteClientProxy)applicationContext.getBean(name);
                }
                //对proxy设置初始值
                DefaultKBRouteClientProxy defaultKBRouteClientProxy = (DefaultKBRouteClientProxy)proxy;
                defaultKBRouteClientProxy.setRegister(register);
                defaultKBRouteClientProxy.setUrl(url);
                defaultKBRouteClientProxy.setName(name);
                defaultKBRouteClientProxy.setGroup(group);
                defaultKBRouteClientProxy.setGlobalname(globalname);
                defaultKBRouteClientProxy.setAeskey(aeskey);
                defaultKBRouteClientProxy.getKbProtocol().setAeskey(aeskey);
                defaultKBRouteClientProxy.getKbProtocol().setApplicationContext(applicationContext);
                //将proxy设置到当前bean对象中
                field.setAccessible(true);
                try {
                    field.set(bean,defaultKBRouteClientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                KBClientRegisterManager.getClientRegisterThread().addKBRouteClientProxy(defaultKBRouteClientProxy);
            }
        }
        return bean;
    }

    private ApplicationContext applicationContext;

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
}
