package cn.kanmars.kb.core.beanDefinitionParse;

import cn.kanmars.kb.server.conf.KBServerConfig;
import cn.kanmars.kb.util.StringUtils;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by kanmars2009 on 2016/3/7.
 */
public class KBServerConfigBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {
    protected Class getBeanClass(Element element ) {
        return KBServerConfig.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String bindip = element.getAttribute("bindip");
        String bingport = element.getAttribute("bindport");
        String minthreadpool = element.getAttribute("minthreadpool");
        String maxthreadpool = element.getAttribute("maxthreadpool");
        String idleTimeout = element.getAttribute("idleTimeout");
        String register = element.getAttribute("register");
        if(StringUtils.isNotEmpty(bindip)){
            builder.addPropertyValue("bindip",bindip);
        }
        if(StringUtils.isNotEmpty(bingport)){
            builder.addPropertyValue("bingport",Integer.parseInt(bingport));
        }
        if(StringUtils.isNotEmpty(minthreadpool)){
            builder.addPropertyValue("minthreadpool",Integer.parseInt(minthreadpool));
        }
        if(StringUtils.isNotEmpty(maxthreadpool)){
            builder.addPropertyValue("maxthreadpool",Integer.parseInt(maxthreadpool));
        }
        if(StringUtils.isNotEmpty(idleTimeout)){
            builder.addPropertyValue("idleTimeout",Integer.parseInt(idleTimeout));
        }
        if(StringUtils.isNotEmpty(register)){
            builder.addPropertyValue("register",register);
        }
    }
}
