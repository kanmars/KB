package cn.kanmars.kb.core.namespaceHandler;

import cn.kanmars.kb.core.beanDefinitionParse.KBServerConfigBeanDefinitionParser;
import cn.kanmars.kb.core.beanDefinitionParse.KBClientConfigBeanDefinitionParser;
import cn.kanmars.kb.properties.beanDefinitionParse.KBPropertiesBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
/**
 * Created by kanmars2009 on 2016/3/7.
 */
public class KBNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser( "server" , new KBServerConfigBeanDefinitionParser());
        registerBeanDefinitionParser( "client" , new KBClientConfigBeanDefinitionParser());
        registerBeanDefinitionParser( "properties" , new KBPropertiesBeanDefinitionParser());
    }
}
