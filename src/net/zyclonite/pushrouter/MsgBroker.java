/*
 * PushRouter
 *
 * Copyright 2011   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.pushrouter;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.servlet.AsyncContext;

/**
 *
 * @author zyclonite
 */
public final class MsgBroker {

    private static MsgBroker instance;
    private static Queue<AsyncContext> contextqueue;
    private static BlockingQueue<Message> messagequeue;

    static {
        instance = new MsgBroker();
    }

    private MsgBroker() {
        contextqueue = new ConcurrentLinkedQueue<AsyncContext>();
        messagequeue = new LinkedBlockingQueue<Message>(1000);
    }

    public static void registerListener(final AsyncContext acontext) {
        if(!contextqueue.contains(acontext)){
            contextqueue.add(acontext);
        }
    }

    public static void unregisterListener(final AsyncContext acontext) {
        if (!contextqueue.isEmpty()) {
            contextqueue.remove(acontext);
        }
    }

    public static Queue<AsyncContext> getListeners() {
        return contextqueue;
    }

    public static Message takeMessage() throws InterruptedException {
        return messagequeue.take();
    }

    public static void putMessage(final Message message) throws InterruptedException {
        messagequeue.put(message);
    }

    public static void cleanup() {
        messagequeue.clear();
        contextqueue.clear();
    }

    public static MsgBroker getInstance() {
        return instance;
    }
}
