package cn.kanmars.kb.client.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;

/**
 * Created by baolong on 2016/3/7.
 */
@Documented //文档
@Retention(RetentionPolicy.RUNTIME) //在运行时可以获取
@Target(ElementType.FIELD ) //作用到类，方法，接口上等
@Inherited //子类会继承
public @interface KBRouteClient {
    //注册中心
    String register() default "";
    //url，如果存在，则直接访问该url
    String url() default "";
    //客户端的名称
    String name() default "";
    //分组
    String group() default "KB_DEFAULT";
    //全局名称
    String globalname() ;
    //通信要用的aes密钥
    String aeskey() default "";
}
