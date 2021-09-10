package com.ct.myim.im.service;

import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.ct.myim.common.constant.MsgType;
import com.ct.myim.common.utils.IdUtils;
import com.ct.myim.framework.redis.TokenService;
import com.ct.myim.im.entity.*;
import com.ct.myim.sockent.manager.WsClientManager;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 通知公共类
 */
@Service
public class NoticeService {

    @Value("${file.download.abspath.prefix}")
    private String fileUrl;

    @Resource
    private UserService userService;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private TokenService tokenService;
    @Resource
    private MsgService msgService;


    /**
     *通知新增联系人
     * @param toUserName  通知谁
     * @param addUserName 新增谁
     */
    public void appendContact(String toUserName,String addUserName) {
        Channel channel = WsClientManager.getInstance().getChannel(toUserName);
        if (channel != null && channel.isActive()) {
            SocketMsg socketMsg = new SocketMsg();
            socketMsg.setId(IdUtils.fastSimpleUUID());
            socketMsg.setHttpType(MsgType.NOTICE_ADD_USER);
            User userOne = userService.getUserByuserName(addUserName);
            Dict dict = new Dict();
            dict.set("id",userOne.getUserName());
            dict.set("displayName",userOne.getNickName());
            dict.set("avatar",userOne.getAvatar());
            dict.set("index",userOne.getIndex());
            if(userOne.isGroup()){
                dict.set("isGroup",userOne.isGroup());
                dict.set("root",userOne.getRoot());
            }
            socketMsg.setContent(JSON.toJSONString(dict));
            WsClientManager.getInstance().sendMsg(toUserName,JSON.toJSONString(socketMsg));
        }
    }

    /**
     *通知删除联系人
     * @param toUserName
     * @param deleteUserName
     */
    public void removeContact(String toUserName,String deleteUserName){
        Channel channel = WsClientManager.getInstance().getChannel(toUserName);
        if (channel != null && channel.isActive()) {
            SocketMsg socketMsg = new SocketMsg();
            socketMsg.setId(IdUtils.fastSimpleUUID());
            socketMsg.setHttpType(MsgType.NOTICE_DELETE_USER);
            socketMsg.setContent(deleteUserName);
            WsClientManager.getInstance().sendMsg(toUserName,JSON.toJSONString(socketMsg));
        }
    }

    /**
     *通知更新联系人
     * @param updateUserName
     */
    public void updateContact(String updateUserName){
            User user = userService.getUserByuserName(updateUserName);
            Dict dict = new Dict();
            dict.set("id",user.getUserName());
            dict.set("displayName",user.getNickName());
            dict.set("avatar",user.getAvatar());
            dict.set("index",user.getIndex());
            if(user.isGroup()){
                dict.set("isGroup",user.isGroup());
                dict.set("root",user.getRoot());
            }
            List<ContactsUser> list = mongoTemplate.find(new Query(Criteria.where("userName").is(user.getUserName())), ContactsUser.class);
            for (ContactsUser contactsUser : list) {
                Channel channel = WsClientManager.getInstance().getChannel(contactsUser.getContactsUserName());
                if(channel != null && channel.isActive()){
                    SocketMsg socketMsg = new SocketMsg();
                    socketMsg.setId(IdUtils.fastSimpleUUID());
                    socketMsg.setHttpType(MsgType.UPDATE_USER);
                    int i = msgService.getMsgSize(contactsUser.getContactsUserName(),user.getUserName(),user.isGroup());
                    dict.set("unread",i);
                    SocketMsg socketMsgs = msgService.getMsgLastSendTimeOrLastContent(contactsUser.getContactsUserName(), user.getUserName(),user.isGroup());
                    if(socketMsgs != null){
                        dict.set("lastSendTime",socketMsgs.getSendTime());
                        dict.set("lastContent",socketMsgs.getContent());
                    }
                    socketMsg.setContent(JSON.toJSONString(dict));
                    WsClientManager.getInstance().sendMsg(contactsUser.getContactsUserName(),JSON.toJSONString(socketMsg));
                }
        }
    }

    /**
     * 系统通知
     * @param toUserName
     * @param msg 通知内容
     */
    public void systemNotification(String toUserName,String msg){
        //通知被添加人
        SocketMsg socketMsg = new SocketMsg();
        socketMsg.setMsgId(IdUtils.fastSimpleUUID());
        socketMsg.setContent(msg);
        socketMsg.setSendTime(new Date().getTime());
        socketMsg.setToContactUserName(toUserName);
        socketMsg.setFormUserName("admin");
        SocketMessage message = new SocketMessage();
        message.setId(socketMsg.getId());
        message.setContent(socketMsg.getContent());
        message.setSendTime(socketMsg.getSendTime());
        message.setStatus("succeed");
        message.setType("text");
        message.setToContactId(socketMsg.getToContactUserName());
        User adminUser = userService.getUserByuserName("admin");
        FromUser fromUser = new FromUser();
        fromUser.setId("admin");
        fromUser.setAvatar(fileUrl + adminUser.getAvatar());
        fromUser.setDisplayName("系统通知");
        message.setFromUser(fromUser);
        socketMsg.setMessage(message);
        socketMsg.setHttpType(MsgType.PRIVATE_CHAT);
        Channel channel = WsClientManager.getInstance().getChannel(toUserName);
        if(channel != null && channel.isActive()){
            WsClientManager.getInstance().sendMsg(socketMsg.getMessage().getToContactId(),JSON.toJSONString(socketMsg));
        }
        mongoTemplate.insert(socketMsg);
    }

    /**
     *  通知已登录用户退出登录
     */
    public void outLogin(String userName){
        Channel channel = WsClientManager.getInstance().getChannel(userName);
        if (channel != null && channel.isActive()) {
            SocketMsg socketMsg = new SocketMsg();
            socketMsg.setId(IdUtils.fastSimpleUUID());
            socketMsg.setHttpType(MsgType.NOTICE_OUT_LOGIN);
            WsClientManager.getInstance().sendMsg(userName,JSON.toJSONString(socketMsg));
        }
    }


    /**
     * 通知删除消息
     * @param id
     */
    public void removeMessage(String userName, String id){
        Channel channel = WsClientManager.getInstance().getChannel(userName);
        if (channel != null && channel.isActive()) {
            SocketMsg socketMsg = new SocketMsg();
            socketMsg.setId(IdUtils.fastSimpleUUID());
            socketMsg.setHttpType(MsgType.NOTICE_DELETE_MSG);
            socketMsg.setContent(id);
            WsClientManager.getInstance().sendMsg(userName,JSON.toJSONString(socketMsg));
        }
    }

    /**
     * 通知撤销消息
     * @param id
     */
    public void revokeMessage(String userName, String id){
        Channel channel = WsClientManager.getInstance().getChannel(userName);
        if (channel != null && channel.isActive()) {
            SocketMsg socketMsg = new SocketMsg();
            socketMsg.setId(IdUtils.fastSimpleUUID());
            socketMsg.setHttpType(MsgType.NOTICE_REVOKE_MSG);
            socketMsg.setContent(id);
            WsClientManager.getInstance().sendMsg(userName,JSON.toJSONString(socketMsg));
        }
    }
}
