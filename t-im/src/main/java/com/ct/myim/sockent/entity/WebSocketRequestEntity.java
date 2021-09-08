package com.ct.myim.sockent.entity;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketRequestEntity {

    //握手请求
    private FullHttpRequest request;
    //websocket请求
    private WebSocketFrame frame;

    public WebSocketRequestEntity(FullHttpRequest request) {
        this.request = request;
    }

    public WebSocketRequestEntity(WebSocketFrame frame) {
        this.frame = frame;
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }

    public WebSocketFrame getFrame() {
        return frame;
    }

    public void setFrame(WebSocketFrame frame) {
        this.frame = frame;
    }
}
