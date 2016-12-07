package cn.kanmars.kb.server.dispatcher;

import cn.kanmars.kb.protocol.AbstractKBProtocol;
import cn.kanmars.kb.protocol.manager.KBProtocolManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by baolong on 2016/3/7.
 */
public class KBDisPatcher {

    private Method method;

    private Object obj;

    private Map annotationMap;

    public AbstractKBProtocol kbProtocol = KBProtocolManager.getDefaultKBProtocol();

    public void exec(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        //报文解析与转发-开始
        //1、生成操作上下文
        Map context = new HashMap();
        //2、对操作上下文设置初始对象
        context.put(AbstractKBProtocol.REQUEST,httpServletRequest);
        context.put(AbstractKBProtocol.RESPONSE, httpServletResponse);
        //3、将request转化为Map
        kbProtocol.transRequestToParam(context);
        //4、将Map交给方法与对象去执行
        Map reqInfo = (Map) context.get(AbstractKBProtocol.REQINFO);
        //如果请求信息不为空，则执行
        if(reqInfo!=null){
            try {
                Map rspInfo = (Map)method.invoke(obj,reqInfo);
                context.put(AbstractKBProtocol.RSPINFO,rspInfo);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        //5、将执行结果转化为response并输出
        kbProtocol.transParamToResponse(context);
        //报文解析与转发-结束
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public AbstractKBProtocol getKbProtocol() {
        return kbProtocol;
    }

    public void setKbProtocol(AbstractKBProtocol kbProtocol) {
        this.kbProtocol = kbProtocol;
    }

    public Map getAnnotationMap() {
        return annotationMap;
    }

    public void setAnnotationMap(Map annotationMap) {
        this.annotationMap = annotationMap;
    }
}
