package com.ct.myim.im.handler;

import com.alibaba.fastjson.JSON;
import com.ct.myim.im.entity.SocketMsg;
import com.ct.myim.im.service.UserService;
import com.ct.myim.sockent.manager.WsClientManager;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class VideoCallHandler {

    @Resource
    private UserService userService;

    public void inquiry(SocketMsg sockeMsg){
        Channel channel = WsClientManager.getInstance().getChannel(sockeMsg.getToContactUserName());
        if(channel != null && channel.isActive()){
            WsClientManager.getInstance().sendMsg(sockeMsg.getToContactUserName(), JSON.toJSONString(sockeMsg));
        }
    }

    public void send(SocketMsg sockeMsg){
        Channel channel = WsClientManager.getInstance().getChannel(sockeMsg.getToContactUserName());
        if(channel != null && channel.isActive()){
            WsClientManager.getInstance().sendMsg(sockeMsg.getToContactUserName(), JSON.toJSONString(sockeMsg));
        } else {
            sockeMsg.setContent("NO");
            WsClientManager.getInstance().sendMsg(sockeMsg.getFormUserName(),JSON.toJSONString(sockeMsg));
        }

    }
}
