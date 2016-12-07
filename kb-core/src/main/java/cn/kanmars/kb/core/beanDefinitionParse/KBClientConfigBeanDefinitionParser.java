package cn.kanmars.kb.core.beanDefinitionParse;

import cn.kanmars.kb.client.conf.KBClientConfig;
import cn.kanmars.kb.util.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by baolong on 2016/3/7.
 */
public class KBClientConfigBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {
    protected Class getBeanClass(Element element ) {
        return KBClientConfig.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String basepackage = element.getAttribute("basepackage");
        String retries = element.getAttribute("retries");
        if(StringUtils.isNotEmpty(basepackage)){
            builder.addPropertyValue("basepackage",basepackage);
        }
        if(StringUtils.isNotEmpty(retries)){
            builder.addPropertyValue("retries",retries);
        }
    }
}
