package com.ct.myim.im.handler;

import com.alibaba.fastjson.JSON;
import com.ct.myim.common.constant.MsgType;
import com.ct.myim.common.utils.IdUtils;
import com.ct.myim.framework.distruptor.base.BaseEvent;
import com.ct.myim.framework.distruptor.base.MessageProducer;
import com.ct.myim.im.cache.ContactsUserCache;
import com.ct.myim.im.entity.ContactsUser;
import com.ct.myim.im.entity.SocketMsg;
import com.ct.myim.im.entity.User;
import com.ct.myim.im.service.UserService;
import com.ct.myim.sockent.manager.WsClientManager;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GroupMsgHandler {

    @Value("${file.download.abspath.prefix}")
    private String fileUrl;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private UserService userService;

    @Resource
    private ContactsUserCache contactsUserCache;



    public void send(SocketMsg sockeMsg) {
        List<ContactsUser> userList = contactsUserCache.getContactsUserCache(sockeMsg.getMessage().getToContactId());
        sockeMsg.setSendTime(sockeMsg.getMessage().getSendTime());
        sockeMsg.setHttpType(sockeMsg.getHttpType());
        sockeMsg.setFormUserName(sockeMsg.getMessage().getToContactId());
        sockeMsg.setToContactUserName(sockeMsg.getMessage().getFromUser().getId());
        sockeMsg.getMessage().setStatus("succeed");
        sockeMsg.setMsgId(sockeMsg.getMessage().getId());
        sockeMsg.setId(IdUtils.fastSimpleUUID());
        mongoTemplate.insert(sockeMsg);
        for (ContactsUser contactsUser : userList) {
            User user = userService.getUserByuserName(contactsUser.getContactsUserName());
            Channel channel = WsClientManager.getInstance().getChannel(user.getUserName());
            if (channel != null && channel.isActive()) {
                String fileId = sockeMsg.getMessage().getContent();
                if(!user.getUserName().equals(sockeMsg.getToContactUserName())){
                    if(sockeMsg.getMessage().getFileSize() > 0){
                        sockeMsg.getMessage().setContent(fileUrl + fileId);
                    }
                    WsClientManager.getInstance().sendMsg(user.getUserName(), JSON.toJSONString(sockeMsg));
                }
            }
        }
    }
}
