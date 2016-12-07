package cn.kanmars.kb.util;

/**
 * Created by baolong on 2016/3/10.
 */
public class LoggerUtil {
    public static int DEBUG = 0;
    public static int INFO  = 1;
    public static int ERROR = 2;

    public static int CURRENT_LEVEL = DEBUG;

    public static void print(int level,String s,Exception e){
        if(level>=CURRENT_LEVEL){
            System.out.println(DateUtils.getCurrTime()+"  "+s);
            if(e!=null){
                e.printStackTrace();
            }
        }
    }
}
