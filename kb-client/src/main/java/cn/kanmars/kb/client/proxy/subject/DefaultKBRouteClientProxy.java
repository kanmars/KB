package cn.kanmars.kb.client.proxy.subject;

import cn.kanmars.kb.client.client.KBClient;
import cn.kanmars.kb.client.client.manager.ClientManager;
import cn.kanmars.kb.client.proxy.KBRouteClientProxy;
import cn.kanmars.kb.protocol.AbstractKBProtocol;
import cn.kanmars.kb.util.StringUtils;
import net.sf.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by baolong on 2016/3/8.
 */
public class DefaultKBRouteClientProxy extends KBRouteClientProxy implements ApplicationContextAware,BeanNameAware {

    private ApplicationContext applicationContext;
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private String name;
    public void setBeanName(String name) {
        this.name = name;
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

    @Override
    public Map exec(Map param) {
        try{
            //获取代理池
            KBClient kbClient = ClientManager.getClient();
            Map context = new HashMap();
            context.put(AbstractKBProtocol.REQINFO,param);//REQINFO(客户端)->REQUEST(网络)->REQINFO(服务器端)的逻辑
            //将Map请求转换为请求报文
            kbProtocol.transParamToRequest(context);
            String content = (String)context.get(AbstractKBProtocol.REQUEST);
            //调用远程服务开始
            String resourcesUrl = url;
            if(StringUtils.isEmpty(url)){
                throw new RuntimeException("KBRouteCLientProxy尚未初始化");
            }
            if(resourcesUrl.endsWith("/")){
                resourcesUrl = resourcesUrl+globalname;
            }else{
                resourcesUrl = resourcesUrl+"/"+globalname;
            }
            String response = null;
            try{
                //如果没有发生异常,则尝试从第一个调用，如果发生异常，则删除第一个
                response = (String)ClientManager.getClient().send(resourcesUrl, content);
            }catch (Exception e){
                url = null;
            }
            if( url == null && bakUrl != null && response == null ){
                int count = retryTimes;
                for(String url : bakUrl){
                    if(count<=0)break;
                    count--;
                    try{
                        //如果没有发生异常,则尝试从第一个调用，如果发生异常，则删除第一个
                        response = (String)ClientManager.getClient().send(resourcesUrl, content);
                        break;
                    }catch (Exception e){
                        url = null;
                    }
                }
            }
            //唯一正确出口
            if(response!=null){
                //调用远程服务结束
                context.put(AbstractKBProtocol.RESPONSE, response);
                kbProtocol.transResponseToParam(context);
                Map result = (Map) context.get(AbstractKBProtocol.RSPMSG);//RSPMSG<-RSPINFO<-RESPONSE<-RSPINFO<-RSPMSG的逻辑
                return result;
            }
            //调用失败
            if(1==1)throw new RuntimeException("调用失败");
        }catch (Exception e){
            throw new RuntimeException("调用失败");
        }
        return null;
    }




}
