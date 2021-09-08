package com.ct.myim.im.handler;

import com.alibaba.fastjson.JSON;
import com.ct.myim.common.constant.MsgType;
import com.ct.myim.common.utils.IdUtils;
import com.ct.myim.im.entity.SocketMsg;
import com.ct.myim.sockent.manager.WsClientManager;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class PersonalMsgHandler {

    @Value("${file.download.abspath.prefix}")
    private String fileUrl;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void send(SocketMsg sockeMsg){
        sockeMsg.setId(IdUtils.fastSimpleUUID());
        sockeMsg.setToContactUserName(sockeMsg.getMessage().getToContactId());
        sockeMsg.setContent(sockeMsg.getMessage().getContent());
        sockeMsg.setSendTime(sockeMsg.getMessage().getSendTime());
        sockeMsg.getMessage().setStatus("succeed");
        sockeMsg.setMsgId(sockeMsg.getMessage().getId());
        Channel channel = WsClientManager.getInstance().getChannel(sockeMsg.getMessage().getToContactId());
        if(channel != null && channel.isActive()){
            String fileId = sockeMsg.getMessage().getContent();
            if(sockeMsg.getMessage().getFileSize() > 0){
                sockeMsg.getMessage().setContent(fileUrl + fileId);
            }
            WsClientManager.getInstance().sendMsg(sockeMsg.getMessage().getToContactId(),JSON.toJSONString(sockeMsg));
            sockeMsg.getMessage().setContent(fileId);
            sockeMsg.setMsgType(MsgType.SEND_OK);
            mongoTemplate.insert(sockeMsg);
        }else{
            sockeMsg.setMsgType(MsgType.SEND_NO);
            mongoTemplate.insert(sockeMsg);
        }
    }

}
