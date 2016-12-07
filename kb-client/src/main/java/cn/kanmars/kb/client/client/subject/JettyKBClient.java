package cn.kanmars.kb.client.client.subject;

import cn.kanmars.kb.client.client.KBClient;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;

import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * Created by baolong on 2016/3/8.
 */
public class JettyKBClient extends KBClient {
    HttpClient httpClient = new HttpClient();
    public void start() throws Exception {
        httpClient.setFollowRedirects(false);
        httpClient.start();
    }

    @Override
    public void stop() throws Exception {
        httpClient.stop();
    }

    public Object send(String url,String content) throws Exception {
        Request request = httpClient.newRequest(url);
        request.content(new StringContentProvider(content));
        ContentResponse response = request.send();
        if(response.getStatus()!=200){
            return null;
        }
        String rspContent = new String(response.getContent());
        return rspContent;
    }
}
