package cn.kanmars.kb.protocol;

import net.sf.json.JSONObject;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Created by baolong on 2016/3/7.
 */
public abstract class AbstractKBProtocol {

    protected int DEFAULT_READ_BUFFER_SIZE = 1024*10;
    protected int DEFAULT_WRITE_BUFFER_SIZE = 1024*10;
    protected String charset = "UTF-8";
    protected String aeskey = null;

    public static final String REQUEST = "REQUEST";
    public static final String RESPONSE = "RESPONSE";
    public static final String REQINFO = "REQINFO";
    public static final String RSPINFO = "RSPINFO";
    public static final String RSPDESC = "RSPDESC";
    public static final String RSPMSG = "RSPMSG";
    public static final String RSPCODE = "RSPCODE";
    public static final String RSPCODE_SUCCESS="0000";
    public static final String RSPCODE_FAILTURE="0002";

    private ApplicationContext applicationContext;

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 将servletRequest转化为一个Map
     * @param context
     * @return
     */
    public abstract void transRequestToParam(Object context);

    /**
     * 将json转化为一个ServletRequest
     * @param context
     * @return
     */
    public abstract void transParamToRequest(Object context) throws Exception;

    /**
     * 将一个servletResponse转化为一个Map
     * @param context
     * @return
     */
    public abstract void transResponseToParam(Object context) throws Exception;

    /**
     * 将一个Map转化为一个ServletResponse
     * @param context
     * @return
     */
    public abstract void transParamToResponse(Object context);


    public String getAeskey() {
        return aeskey;
    }

    public void setAeskey(String aeskey) {
        this.aeskey = aeskey;
    }

    public int getDEFAULT_READ_BUFFER_SIZE() {
        return DEFAULT_READ_BUFFER_SIZE;
    }

    public void setDEFAULT_READ_BUFFER_SIZE(int DEFAULT_READ_BUFFER_SIZE) {
        this.DEFAULT_READ_BUFFER_SIZE = DEFAULT_READ_BUFFER_SIZE;
    }

    public int getDEFAULT_WRITE_BUFFER_SIZE() {
        return DEFAULT_WRITE_BUFFER_SIZE;
    }

    public void setDEFAULT_WRITE_BUFFER_SIZE(int DEFAULT_WRITE_BUFFER_SIZE) {
        this.DEFAULT_WRITE_BUFFER_SIZE = DEFAULT_WRITE_BUFFER_SIZE;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
