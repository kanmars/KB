package cn.kanmars.kb.server.handler;

import cn.kanmars.kb.server.dispatcher.KBDisPatcher;
import cn.kanmars.kb.server.kernel.KBKernel;
import cn.kanmars.kb.util.LoggerUtil;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by kanmars2009 on 2016/3/7.
 */
public class KBMetaHandler extends AbstractHandler {

    private KBKernel kbKernel;

    public KBMetaHandler() {
    }

    public KBMetaHandler(KBKernel kbKernel) {
        this.kbKernel = kbKernel;
    }

    public KBKernel getKbKernel() {
        return kbKernel;
    }

    public void setKbKernel(KBKernel kbKernel) {
        this.kbKernel = kbKernel;
    }

    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        LoggerUtil.print(LoggerUtil.DEBUG, "收到请求["+s+"]", null);
        KBDisPatcher kbDisPatcher =  kbKernel.getKBDispatcher(s);
        if(kbDisPatcher!=null){
            kbDisPatcher.exec(httpServletRequest,httpServletResponse);
        }else{
            rsp404(httpServletResponse);
        }
    }

    public void rsp404(HttpServletResponse httpServletResponse){
        try {
            httpServletResponse.setStatus(404);
            httpServletResponse.getOutputStream().flush();
            httpServletResponse.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
