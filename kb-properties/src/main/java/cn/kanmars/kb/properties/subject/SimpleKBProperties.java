package cn.kanmars.kb.properties.subject;

import cn.kanmars.kb.properties.AbstractKBProperties;

/**
 * Created by kanmars2009 on 2016/3/6.
 */
public class SimpleKBProperties extends AbstractKBProperties {
    private String value;

    public String getProperties() {
        return value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
