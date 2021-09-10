package com.ct.myim.im.handler;

import com.alibaba.fastjson.JSON;
import com.ct.myim.common.constant.MsgType;
import com.ct.myim.common.utils.IdUtils;
import com.ct.myim.framework.distruptor.base.BaseEvent;
import com.ct.myim.framework.distruptor.base.MessageProducer;
import com.ct.myim.im.entity.SocketMsg;
import com.ct.myim.sockent.manager.WsClientManager;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PersonalMsgHandler {

    @Value("${file.download.abspath.prefix}")
    private String fileUrl;

    @Resource
    private MongoTemplate mongoTemplate;


    public void send(SocketMsg sockeMsg){
        sockeMsg.setId(IdUtils.fastSimpleUUID());
        sockeMsg.setFormUserName(sockeMsg.getMessage().getFromUser().getId());
        sockeMsg.setToContactUserName(sockeMsg.getMessage().getToContactId());
        sockeMsg.setSendTime(sockeMsg.getMessage().getSendTime());
        sockeMsg.getMessage().setStatus("succeed");
        sockeMsg.setMsgId(sockeMsg.getMessage().getId());
        mongoTemplate.insert(sockeMsg);
        Channel channel = WsClientManager.getInstance().getChannel(sockeMsg.getMessage().getToContactId());
        if(channel != null && channel.isActive()){
            String fileId = sockeMsg.getMessage().getContent();
            if(sockeMsg.getMessage().getFileSize() > 0){
                sockeMsg.getMessage().setContent(fileUrl + fileId);
            }
            WsClientManager.getInstance().sendMsg(sockeMsg.getMessage().getToContactId(),JSON.toJSONString(sockeMsg));
        }
    }

}
