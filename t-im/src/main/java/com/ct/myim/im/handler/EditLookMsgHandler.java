package com.ct.myim.im.handler;

import com.ct.myim.im.cache.MsgLookCalipersCache;
import com.ct.myim.im.entity.MsgLookCalipers;
import com.ct.myim.im.entity.SocketMsg;
import com.ct.myim.im.entity.User;
import com.ct.myim.im.service.MsgService;
import com.ct.myim.im.service.UserService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class EditLookMsgHandler {

    @Resource
    private MsgService service;
    @Resource
    private UserService userService;
    @Resource
    private MsgLookCalipersCache msgLookCalipersCache;

    public void edit(SocketMsg sockeMsg){
        msgLookCalipersCache.setMsgLookCalipersCache(sockeMsg.getFormUserName(),sockeMsg.getToContactUserName());
    }
}
