package cn.kanmars;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 */
public class ServerDemo {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext c = new ClassPathXmlApplicationContext("classpath:spring-server.xml");
        Object cc = c.getBean("kbserver1");
    }
}
