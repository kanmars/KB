package cn.kanmars.kb.server.beanpostProcessor;

import cn.kanmars.kb.server.annotation.KBRouteServer;
import cn.kanmars.kb.server.dispatcher.KBDisPatcher;
import cn.kanmars.kb.server.kernel.KBKernel;
import cn.kanmars.kb.server.kernel.manager.KBKernelManager;
import cn.kanmars.kb.server.server.manager.ServerManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by baolong on 2016/3/8.
 */
public class KBRouteServerBeanPostProcessor implements BeanPostProcessor,ApplicationContextAware {

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class clazz = bean.getClass();
        Method[] methods = clazz.getMethods();
        for(Method method:methods){
            KBRouteServer kbRouteServer = method.getAnnotation(KBRouteServer.class);
            if(kbRouteServer!=null){
                KBDisPatcher kbDisPatcher = new KBDisPatcher();
                kbDisPatcher.getKbProtocol().setAeskey(kbRouteServer.aeskey());//设置通信密钥
                kbDisPatcher.getKbProtocol().setApplicationContext(applicationContext);
                kbDisPatcher.setMethod(method);//设置当前方法
                kbDisPatcher.setObj(bean);//设置springBean
                Map annotationMap = new HashMap();
                annotationMap.put("group",kbRouteServer.group());
                annotationMap.put("globalname",kbRouteServer.globalname());
                annotationMap.put("servername",kbRouteServer.servername());
                annotationMap.put("aeskey", kbRouteServer.aeskey());
                kbDisPatcher.setAnnotationMap(annotationMap);
                //检查是否有重复的KbRoute
                ServerManager.addKbRouteServer(kbRouteServer.group(),kbRouteServer.globalname(),kbRouteServer.servername());

                KBKernel kbKernel = KBKernelManager.getOrInstanceKBKernel(kbRouteServer.servername());
                kbKernel.register("/" + kbRouteServer.globalname(), kbDisPatcher);

            }
        }
        return bean;
    }

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
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
