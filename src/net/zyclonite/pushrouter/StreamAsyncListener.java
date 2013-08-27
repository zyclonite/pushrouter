/*
 * PushRouter
 *
 * Copyright 2011   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.pushrouter;

import java.io.IOException;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;

/**
 *
 * @author zyclonite
 */
public class StreamAsyncListener implements AsyncListener {

    public void onComplete(final AsyncEvent event) throws IOException {
        MsgBroker.unregisterListener(event.getAsyncContext());
    }

    public void onTimeout(final AsyncEvent event) throws IOException {
        MsgBroker.unregisterListener(event.getAsyncContext());
        event.getAsyncContext().dispatch();
    }

    public void onError(final AsyncEvent event) throws IOException {
        MsgBroker.unregisterListener(event.getAsyncContext());
    }

    public void onStartAsync(final AsyncEvent event) throws IOException {
        //not used
    }
}
