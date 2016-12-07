package cn.kanmars.kb.properties.beanDefinitionParse;

import cn.kanmars.kb.properties.subject.SimpleKBProperties;
import cn.kanmars.kb.util.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by baolong on 2016/3/7.
 */
public class KBPropertiesBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {
    protected Class getBeanClass(Element element ) {
        return SimpleKBProperties.class;
    }
    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String value = element.getAttribute("value");
        if(StringUtils.isNotEmpty(value)){
            builder.addPropertyValue("value",value);
        }
    }
}
