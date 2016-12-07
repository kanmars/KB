package cn.kanmars.kb.server.server.subject;

import cn.kanmars.kb.server.conf.KBServerConfig;
import cn.kanmars.kb.server.handler.KBMetaHandler;
import cn.kanmars.kb.server.kernel.KBKernel;
import cn.kanmars.kb.server.kernel.manager.KBKernelManager;
import cn.kanmars.kb.server.register.subject.DefaultKBServerRegister;
import cn.kanmars.kb.server.server.KBServer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kanmars2009 on 2016/3/7.
 */
public class JettyKBServer implements KBServer {
    private KBServerConfig kbServerConfig;
    private KBKernel kbKernel;
    private Server server;
    private DefaultKBServerRegister.ServerRegisterThread dsrt = new DefaultKBServerRegister.ServerRegisterThread();
    private ExecutorService executorService;

    public void init(KBServerConfig kbServerConfig) throws Exception {
        this.kbServerConfig = kbServerConfig;
    }

    public KBServerConfig getKbServerConfig() {
        return kbServerConfig;
    }

    public KBKernel getKbKernel() {
        return kbKernel;
    }

    public void start(KBServerConfig kbServerConfig) throws Exception {
        //设置线程池
        ThreadPool threadPool = new QueuedThreadPool(kbServerConfig.getMaxthreadpool(),kbServerConfig.getMinthreadpool(),kbServerConfig.getIdleTimeout());
        server = new Server(threadPool);
        //设置connector
        ServerConnector connector = new ServerConnector(server);
        connector.setHost(kbServerConfig.getBindip());
        connector.setPort(kbServerConfig.getBingport());
        connector.setIdleTimeout(kbServerConfig.getIdleTimeout());
        server.setConnectors(new Connector[]{connector});
        //设置KBKernel
        kbKernel = KBKernelManager.getOrInstanceKBKernel(kbServerConfig.getBeanName());
        kbKernel.setKbServer(this);
        //设置Handler
        server.setHandler(new KBMetaHandler(kbKernel));
        //启动
        server.start();
        //理论上说，此处需要join，但实际情况由于server交给spring托管，此处不需要join
        dsrt.setRegisterAddress(this.getKbServerConfig().getRegister());
        dsrt.setKbKernel(kbKernel);
        executorService = Executors.newFixedThreadPool(3);
        executorService.execute(dsrt);
    }

    public void stop(KBServerConfig kbServerConfig) throws Exception {
        server.stop();
    }

    public void join() throws Exception {
        server.join();
    }
}
