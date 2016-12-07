package cn.kanmars;

import cn.kanmars.client.DDD;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by baolong on 2016/3/8.
 */
public class ClientDemo {


    public static void main(String[] args){
        ClassPathXmlApplicationContext c = new ClassPathXmlApplicationContext("classpath:spring-client.xml");
        DDD ddd = (DDD)c.getBean(DDD.class);
        ddd.exec();
    }

}
