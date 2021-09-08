package com.ct.myim.sockent.manager;

import com.alibaba.fastjson.JSON;
import com.ct.myim.common.constant.MsgType;
import com.ct.myim.common.utils.SpringUtils;
import com.ct.myim.common.utils.StringUtils;
import com.ct.myim.framework.redis.TokenService;
import com.ct.myim.framework.web.entity.LoginUser;
import com.ct.myim.sockent.entity.MsgEntity;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WsClientManager {

    private TokenService tokenService;

    //单例
    private static WsClientManager instance = new WsClientManager();
    private WsClientManager(){
        tokenService = SpringUtils.getBean(TokenService.class);
    }
    public static WsClientManager getInstance(){
        return instance;
    }

    //socket自定义ID与信道的对应关系
    private Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    //添加信道
    public String putChannel(String token, Channel channel){
        if(StringUtils.isEmpty(token)){
            return null;
        }
        LoginUser loginUser = tokenService.getLoginUser(token);
        if(loginUser == null){
            return null;
        }
        this.channelMap.put(loginUser.getUser().getUserName(), channel);
        return loginUser.getUser().getUserName();
    }

    //获取信道
    public Channel getChannel(String id){
        return channelMap.get(id);
    }

    //删除信道
    public void removeChannel(String id){
        this.channelMap.remove(id);
    }

    /**
     * 发送消息
     * @param userName
     * @param msg
     */
    public void sendMsg(String userName, String msg){
        TextWebSocketFrame frame = new TextWebSocketFrame(msg);
        Channel channel = channelMap.get(userName);
        if(channel != null){
            channel.writeAndFlush(frame);
        }
    }

    //群发消息
    public void sendMsg2All(String msg){
        for(Channel channel : channelMap.values()){
            TextWebSocketFrame frame = new TextWebSocketFrame(msg);
            channel.writeAndFlush(frame);
        }
    }

    //处理消息
    public void handleMsg(String msgJson){
        MsgEntity msgVo = JSON.parseObject(msgJson, MsgEntity.class);
        if(msgVo.getType() == MsgType.PRIVATE_CHAT){
            this.sendMsg(msgVo.getToId(), msgJson);
            this.sendMsg(msgVo.getFromId(), msgJson);
        }else if(msgVo.getType() == MsgType.GROUP_CHAT){
            this.sendMsg2All(msgJson);
        }
    }

}