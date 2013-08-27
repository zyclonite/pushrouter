/*
 * PushRouter
 *
 * Copyright 2011   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.pushrouter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StreamServlet extends HttpServlet {
    
    private ExecutorService threadpool = null;
    private static final Log LOG = LogFactory.getLog(StreamServlet.class);
    
    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        final int threads = Integer.valueOf(getInitParameter("listenerthreads"));
        threadpool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            threadpool.execute(new MessageDispatcher());
        }
    }
    
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("utf-8");
        res.setHeader("Cache-Control", "private");
        res.setHeader("Pragma", "no-cache");
        
        final AsyncContext acontext = req.startAsync();
        acontext.setTimeout(30 * 1000);
        acontext.addListener(new StreamAsyncListener());
        MsgBroker.registerListener(acontext);
    }
    
    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        String line = null;
        Message message = new Message();
        final StringBuilder response = new StringBuilder();
        final String pathinfo = req.getPathInfo();
        if(pathinfo != null) {
            final String filter = pathinfo.substring(1, pathinfo.length()); 
            message.setHeader(filter);
        }
        try {
            final BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            message.setBody(response.toString());
            MsgBroker.putMessage(message);
        } catch (InterruptedException ex) {
            LOG.warn("Cannot send Message to memory Queue: " + ex.getMessage(), ex.fillInStackTrace());
        }
    }
    
    @Override
    public void destroy() {
        try {
            threadpool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            LOG.warn(ex.getMessage());
        }
        MsgBroker.cleanup();
    }
}
