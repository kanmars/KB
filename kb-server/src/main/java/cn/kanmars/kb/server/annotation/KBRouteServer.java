package cn.kanmars.kb.server.annotation;

import java.lang.annotation.*;

/**
 * Created by baolong on 2016/3/7.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface KBRouteServer {
    //分组
    String group() default "KB_DEFAULT";
    //全局名称
    String globalname() ;
    //要注册的server的名称
    String servername();
    //通信要用的aes密钥
    String aeskey() default "";
}
