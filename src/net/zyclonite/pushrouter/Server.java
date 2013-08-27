/*
 * PushRouter
 *
 * Copyright 2011   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.pushrouter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

/**
 *
 * @author zyclonite
 */
public class Server {

    private static final Log LOG = LogFactory.getLog(Server.class);

    public static void main(final String[] args) {
        LOG.info("PushRouter starting...");
        final Server application = new Server();
        application.run();
    }

    public void run() {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        final AppConfig config = AppConfig.getInstance();

        final int listenerthreads = config.getInt("server.listenerthreads", 2);
        final org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server();
        final SelectChannelConnector connector = new SelectChannelConnector();
        final QueuedThreadPool qtp = new QueuedThreadPool();
        qtp.setName("JettyWorkerPool");
        qtp.setMinThreads(5);
        qtp.setMaxThreads(config.getInt("server.worker", 20));
        connector.setThreadPool(qtp);
        connector.setHost(config.getString("server.bind", "0.0.0.0"));
        connector.setPort(config.getInt("server.port", 8080));
        connector.setMaxIdleTime(120000);
        connector.setLowResourcesMaxIdleTime(60000);
        connector.setLowResourcesConnections(20000);
        connector.setAcceptQueueSize(5000);
        connector.setName("pushrouter");

        server.setConnectors(new Connector[]{(Connector) connector});

        final ServletContextHandler schandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        schandler.setContextPath("/");

        final ServletHolder cholder = schandler.addServlet("net.zyclonite.pushrouter.StreamServlet", "/stream/*");
        cholder.setInitParameter("listenerthreads", String.valueOf(listenerthreads));
        cholder.setAsyncSupported(true);

        final RequestLogHandler requestLogHandler = new RequestLogHandler();
        if (config.getBoolean("server.accesslogs", false)) {
            final NCSARequestLog requestLog = new NCSARequestLog(config.getString("server.logdir", "./") + "web-yyyy_mm_dd.request.log");
            requestLog.setRetainDays(90);
            requestLog.setAppend(true);
            requestLog.setExtended(false);
            requestLog.setLogTimeZone("GMT");
            requestLogHandler.setRequestLog(requestLog);
        }

        final HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{schandler, new DefaultHandler(), requestLogHandler});
        server.setHandler(handlers);

        try {
            server.start();
            LOG.info("PushRouter started");
        } catch (Exception ex) {
            LOG.error("PushRouter could not be started");
        }
    }

    class ShutdownHook extends Thread {

        @Override
        public void run() {
            LOG.info("PushRouter shutting down...");
        }
    }
}
