/*
 * PushRouter
 *
 * Copyright 2011   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.pushrouter;

import java.io.PrintWriter;
import java.util.Queue;
import javax.servlet.AsyncContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author zyclonite
 */
public class MessageDispatcher implements Runnable {

    private static final Log LOG = LogFactory.getLog(MessageDispatcher.class);

    public MessageDispatcher() {
    }

    public void run() {
        boolean done = false;
        String filter = "";
        PrintWriter acWriter = null;
        while (!done) {
            try {
                final Message message = MsgBroker.takeMessage();
                final Queue<AsyncContext> queue = MsgBroker.getListeners();
                for (final AsyncContext ac : queue) {
                    filter = ac.getRequest().getParameter("filter");
                    if ((filter == null) || Util.wildcardMatching(message.getHeader(), filter)) {
                        try {
                            acWriter = ac.getResponse().getWriter();
                            acWriter.println(message.toString());
                            acWriter.flush();
                        } catch (Exception ex) {
                            //LOG.info(ex.getMessage(), ex.fillInStackTrace());
                            MsgBroker.unregisterListener(ac);
                        }
                    }
                }
            } catch (InterruptedException iex) {
                done = true;
                LOG.error(iex.getMessage(), iex.fillInStackTrace());
            }
        }
    }
}
