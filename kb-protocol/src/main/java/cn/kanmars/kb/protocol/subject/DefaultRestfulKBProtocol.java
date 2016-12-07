package cn.kanmars.kb.protocol.subject;

import cn.kanmars.kb.properties.AbstractKBProperties;
import cn.kanmars.kb.protocol.AbstractKBProtocol;
import cn.kanmars.kb.util.*;
import net.sf.json.JSONObject;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by baolong on 2016/3/7.
 */
public class DefaultRestfulKBProtocol extends AbstractKBProtocol {



    /**
     * 将servletRequest转化为一个Map
     * @param context
     * @return
     */
    @Override
    public void transRequestToParam(Object context) {
        Map requestContent = null;
        String requestContentStr = null;
        StringBuilder sb = new StringBuilder();
        InputStream is = null;
        try {
            HttpServletRequest servletRequest = (HttpServletRequest)((Map)context).get(REQUEST);
            is =  servletRequest.getInputStream();
            byte[] tmp = new byte[DEFAULT_READ_BUFFER_SIZE];
            int count =0;
            while((count = is.read(tmp))>=0){
                sb.append(new String(tmp,0,count,charset));
            }
            requestContentStr = sb.toString();
            try{
                requestContent = JSONObject.fromObject(requestContentStr);
            }catch (Exception e){
                ((Map) context).put(RSPCODE, RSPCODE_FAILTURE);
                ((Map) context).put(RSPDESC, "报文格式不正确");
                return;
            }
            //报文格式规范为:
            String reqMessageId = (String)requestContent.get("reqMessageId");//请求ID
            String reqTime      = (String)requestContent.get("reqTime");//请求时间
            String version      = (String)requestContent.get("version");//版本号
            String systemNo     = (String)requestContent.get("systemNo");//系统编号
            String encReqInfo   = (String)requestContent.get("encReqInfo");//加密请求信息，无加密则为Base64数据
            String reqType      = (String)requestContent.get("reqType");//交易类型
            String sign         = (String)requestContent.get("sign");//签名信息
            if(!validateMd5(systemNo,reqTime,encReqInfo,sign,getAnnotationProperties(aeskey)==null?"":getAnnotationProperties(aeskey))){
                ((Map) context).put(RSPCODE, RSPCODE_FAILTURE);
                ((Map) context).put(RSPDESC, "验签不通过");
                return;
            }
            //解密
            String desReqInfo = null;
            if(StringUtils.isNotEmpty(getAnnotationProperties(aeskey))){
                //如果有aeskey，则按照aes加密
                desReqInfo = AESUtil.decryptStr(getAnnotationProperties(aeskey),encReqInfo);
            }else{
                //如果没有aeskey，则按照base64加密
                desReqInfo = new String(Base64Util.decodeMessage(desReqInfo),charset);
            }
            JSONObject reqInfo = null;
            try{
                reqInfo = JSONObject.fromObject(desReqInfo);
            }catch (Exception e){
                ((Map) context).put(RSPCODE,RSPCODE_FAILTURE);
                ((Map) context).put(RSPDESC, "REQINFO报文格式不正确");
                return;
            }
            ((Map) context).put(REQINFO,reqInfo);
            ((Map) context).put(RSPCODE, RSPCODE_SUCCESS);
            ((Map) context).put(RSPDESC, "执行成功");
        } catch (Exception e) {
            e.printStackTrace();
            ((Map) context).put(RSPCODE, RSPCODE_FAILTURE);
            ((Map) context).put(RSPDESC, "系统报文解析异常");
        }
        return;
    }

    /**
     * 进行md5验证签名
     *
     * @param systemNo
     * @param reqTime
     * @param encReqInfo
     * @return
     */
    private String createMd5(String systemNo, String reqTime, String encReqInfo, String md5Key) throws Exception {
        boolean ret = false;
        String formd5Str = systemNo + reqTime + encReqInfo + md5Key;
        String mysign = null;
        try {
            mysign = MD5Util.encodeMessage(formd5Str.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("签名失败");
        }
        return mysign;
    }

    /**
     * 进行md5验证签名
     *
     * @param systemNo
     * @param reqTime
     * @param encReqInfo
     * @param sign
     * @return
     */
    private boolean validateMd5(String systemNo, String reqTime, String encReqInfo, String sign, String md5Key) {
        boolean ret = false;
        String formd5Str = systemNo + reqTime + encReqInfo + md5Key;
        String mysign = null;
        try {
            mysign = MD5Util.encodeMessage(formd5Str.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (mysign.equalsIgnoreCase(sign)) {//忽略大小写的签名
            ret = true;
        }
        return ret;
    }

    /**
     * 将map转化为一个ServletRequest
     *
     * @param othInfo
     * @return
     */
    @Override
    public void transParamToRequest(Object othInfo) throws Exception {
        Map requestContent = new HashMap();
        String reqMessageId = UUID.randomUUID().toString();
        String reqTime = DateUtils.getCurrDateTime();
        String version = "1.0";
        String systemNo = "0001";


        requestContent.put("reqMessageId", reqMessageId);
        requestContent.put("reqTime", reqTime);
        requestContent.put("version", version);
        requestContent.put("systemNo", systemNo);
        Map param = (Map)((Map)othInfo).get(REQINFO);
        String encReqInfo = JSONObject.fromObject(param).toString();
        if(StringUtils.isNotEmpty(getAnnotationProperties(aeskey))){
            //如果有aeskey，则按照aes加密
                encReqInfo = AESUtil.encryptStr(getAnnotationProperties(aeskey), encReqInfo);
        }else{
            //如果没有aeskey，则按照base64加密
            encReqInfo = Base64Util.encodeMessage(encReqInfo.getBytes(charset));
        }
        requestContent.put("encReqInfo",encReqInfo);
        requestContent.put("reqType","RT_01");
        requestContent.put("sign",createMd5(systemNo,reqTime,encReqInfo,getAnnotationProperties(aeskey)));

        ((Map)othInfo).put(REQUEST,JSONObject.fromObject(requestContent).toString());
    }

    /**
     * 将一个servletResponse转化为一个Map
     *
     * @param othInfo
     * @return
     */
    @Override
    public void transResponseToParam(Object othInfo) throws Exception {
        String response = (String)((Map)othInfo).get(RESPONSE);
        JSONObject rspInfo = JSONObject.fromObject(response);
        Object rspMsg = rspInfo.get(RSPMSG);
        if(rspMsg == null){
            ((Map)othInfo).put(RSPMSG,rspMsg);
            return;
        }
        String desReqInfo = rspMsg.toString();
        if(StringUtils.isNotEmpty(getAnnotationProperties(aeskey))){
            //如果有aeskey，则按照aes解密
            desReqInfo = AESUtil.decryptStr(getAnnotationProperties(aeskey),rspMsg.toString());
        }else{
            //如果没有aeskey，则按照base64解密
            desReqInfo = new String(Base64Util.decodeMessage(desReqInfo),charset);
        }
        rspMsg = JSONObject.fromObject(desReqInfo);
        //在交互的出口，修改响应值为null，此处略有不安全，
        if(((JSONObject)rspMsg).isNullObject()){
            ((Map)othInfo).put(RSPMSG,null);
        }else{
            ((Map)othInfo).put(RSPMSG,rspMsg);
        }
    }

    /**
     * 将一个Map转化为一个ServletResponse
     *
     * @param othInfo
     * @return
     */
    @Override
    public void transParamToResponse(Object othInfo) {
        HttpServletResponse httpServletResponse = (HttpServletResponse)((Map)othInfo).get(RESPONSE);
        Map rspInfo = (Map) ((Map) othInfo).get(RSPINFO);
        String rspCode = (String)((Map) othInfo).get(RSPCODE);
        String rspDesc = (String)((Map) othInfo).get(RSPDESC);
        //判断是否是失败，如果失败，则设置响应吗和响应描述
        if(rspCode.equals(RSPCODE_FAILTURE)&&rspInfo==null){
            rspInfo = new HashMap();
            rspInfo.put(RSPCODE,rspCode);
            rspInfo.put(RSPDESC, rspDesc);
        }
        if(rspCode.equals(RSPCODE_FAILTURE)&&rspInfo!=null){
            rspInfo.put(RSPCODE,rspCode);
            rspInfo.put(RSPDESC,rspDesc);
        }
        //判断是否是失败，如果失败，则设置响应吗和响应描述
        try {
            //加密
            String encRspInfo = null;
            if(StringUtils.isNotEmpty(getAnnotationProperties(aeskey))){
                //如果有aeskey，则按照aes加密
                encRspInfo = AESUtil.encryptStr(getAnnotationProperties(aeskey), JSONObject.fromObject(rspInfo).toString());
            }else{
                //如果没有aeskey，则按照base64加密
                encRspInfo = new String(Base64Util.decodeMessage(JSONObject.fromObject(rspInfo).toString()),charset);
            }
            Map rspMap = new HashMap();
            rspMap.put(RSPCODE,rspCode);
            rspMap.put(RSPDESC,rspDesc);
            rspMap.put(RSPMSG,encRspInfo);
            httpServletResponse.getOutputStream().write(JSONObject.fromObject(rspMap).toString().getBytes());
            httpServletResponse.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
            Map resultMap = new HashMap();
            resultMap.put(RSPCODE,"0002");
            resultMap.put(RSPDESC,"输出报文错误");
            try {
                httpServletResponse.getOutputStream().write(JSONObject.fromObject(resultMap).toString().getBytes());
                httpServletResponse.getOutputStream().flush();
            } catch (IOException e1) {
            }
        }
    }

    /**
     * 尝试从spring中获取id为value的kb-properties对象，getProperties，获取到真正的属性
     * @param value
     * @return
     */
    public String getAnnotationProperties(String value){
        if(StringUtils.isEmpty(value)){
            return value;
        }
        Object springBean = null;
        try {
            springBean = this.getApplicationContext().getBean(value);
        }catch (Exception e){
            //e.printStackTrace();
        }
        if(springBean == null) return value;
        if(!(springBean instanceof AbstractKBProperties)) return value;
        value = ((AbstractKBProperties)springBean).getProperties();
        return value;
    }
}
