/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.zyclonite.pushrouter;

/**
 *
 * @author zyclonite
 */
public final class Message {

    private String header = "";
    private String body = "";

    public Message() {
    }

    public Message(String bodyvalue) {
        body = bodyvalue;
    }

    public Message(String bodyvalue, String headervalue) {
        body = bodyvalue;
        header = headervalue;
    }

    public void setBody(String value) {
        body = value;
    }

    public String getBody() {
        return body;
    }

    public void setHeader(String value) {
        header = value;
    }

    public String getHeader() {
        return header;
    }

    @Override
    public String toString() {
        return "{\"header\":\"" + Util.escape(header) + "\",\"body\":\"" + Util.escape(body) + "\"}";
    }
}
