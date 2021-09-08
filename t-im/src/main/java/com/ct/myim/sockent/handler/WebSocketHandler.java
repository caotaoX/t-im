package com.ct.myim.sockent.handler;

import com.ct.myim.common.utils.SpringUtils;
import com.ct.myim.framework.distruptor.base.BaseEvent;
import com.ct.myim.framework.distruptor.base.MessageProducer;
import com.ct.myim.im.config.Config;
import com.ct.myim.sockent.manager.WsClientManager;
import com.ct.myim.sockent.entity.WebSocketRequestEntity;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import com.ct.myim.common.utils.ParamUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketRequestEntity> {

    private MessageProducer messageProducer ;


    //属性名称：握手处理器
    private static final AttributeKey<WebSocketServerHandshaker> HAND_SHAKE_ATTR = AttributeKey.valueOf("HAND_SHAKE");
    //属性名称：websocket自定义id
    private static final AttributeKey<String> SOCKET_ID_ATTR = AttributeKey.valueOf("SOCKET_ID");

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //删除信道
        String id = ctx.channel().attr(SOCKET_ID_ATTR).get();
        if(id == null){
            return;
        }
        WsClientManager.getInstance().removeChannel(id);
        //TODO
        System.out.println("[" + id + "]断开连接。。。");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketRequestEntity requestVo) throws Exception {
        //处理握手
        if(requestVo.getRequest() != null){
            this.handleShake(ctx, requestVo.getRequest());
        }
        //处理websocket数据
        if(requestVo.getFrame() != null){
            this.handleFrame(ctx, requestVo.getFrame());
        }
    }

    //处理握手
    private void handleShake(ChannelHandlerContext ctx, FullHttpRequest request){
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(null, null, false);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), request);
            //保存socket的自定义ID与信道的对应关系
            Map<String, String> params = ParamUtil.getRequestParams(request);
            String id = params.get(Config.SOCKET_ID);
            //用户与通道绑定
            String userName = WsClientManager.getInstance().putChannel(id, ctx.channel());
            if(userName == null){
                ctx.channel().close();
            }else{
                //绑定属性到channel
                ctx.channel().attr(HAND_SHAKE_ATTR).set(handshaker);
                ctx.channel().attr(SOCKET_ID_ATTR).set(userName);
                //TODO
                System.out.println("[" + userName + "]正在握手。。。");
            }
        }
    }

    //处理websocket数据
    private void handleFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            WebSocketServerHandshaker handshaker = ctx.channel().attr(HAND_SHAKE_ATTR).get();
            if(handshaker == null){
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                return;
            }
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 暂仅支持文本消息，不支持二进制消息
        if (! (frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException("暂不支持该消息类型：" + frame.getClass().getName());
        }
        // 处理消息
        String msg = ((TextWebSocketFrame) frame).text();
        if(messageProducer == null ){
            messageProducer = SpringUtils.getBean(MessageProducer.class);
        }
        messageProducer.publish(new BaseEvent(msg));
    }

}
