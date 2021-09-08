package com.ct.myim.im.handler;

import com.alibaba.fastjson.JSON;
import com.ct.myim.common.constant.MsgType;
import com.ct.myim.common.utils.IdUtils;
import com.ct.myim.im.entity.ContactsUser;
import com.ct.myim.im.entity.SocketMsg;
import com.ct.myim.im.entity.User;
import com.ct.myim.sockent.manager.WsClientManager;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupMsgHandler {

    @Value("${file.download.abspath.prefix}")
    private String fileUrl;


    @Autowired
    private MongoTemplate mongoTemplate;

    public void send(SocketMsg sockeMsg) {
        List<ContactsUser> userList = mongoTemplate.find(new Query(Criteria.where("userName").is(sockeMsg.getMessage().getToContactId())), ContactsUser.class);
        SocketMsg socketMsgs = new SocketMsg();
        socketMsgs.setContent(sockeMsg.getMessage().getContent());
        socketMsgs.setSendTime(sockeMsg.getMessage().getSendTime());
        socketMsgs.setHttpType(sockeMsg.getHttpType());
        socketMsgs.setFormUserName(sockeMsg.getMessage().getToContactId());
        socketMsgs.setMessage(sockeMsg.getMessage());
        socketMsgs.getMessage().setStatus("succeed");
        socketMsgs.setMsgId(sockeMsg.getMessage().getId());
        for (ContactsUser contactsUser : userList) {
            User user = mongoTemplate.findOne(new Query(Criteria.where("userName").is(contactsUser.getContactsUserName())), User.class);
            Channel channel = WsClientManager.getInstance().getChannel(user.getUserName());
            socketMsgs.setToContactUserName(user.getUserName());
            socketMsgs.setId(IdUtils.fastSimpleUUID());
            if (channel != null && channel.isActive()) {
                String fileId = socketMsgs.getMessage().getContent();
                if(!user.getUserName().equals(sockeMsg.getFormUserName())){
                    if(sockeMsg.getMessage().getFileSize() > 0){
                        socketMsgs.getMessage().setContent(fileUrl + fileId);
                    }
                    WsClientManager.getInstance().sendMsg(user.getUserName(), JSON.toJSONString(socketMsgs));
                }
                socketMsgs.getMessage().setContent(fileId);
                socketMsgs.setMsgType(MsgType.SEND_OK);
                mongoTemplate.insert(socketMsgs);
            } else {
                socketMsgs.setMsgType(MsgType.SEND_NO);
                mongoTemplate.insert(socketMsgs);
            }
        }
    }
}
