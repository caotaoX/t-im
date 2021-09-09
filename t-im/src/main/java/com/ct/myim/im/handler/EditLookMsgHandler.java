package com.ct.myim.im.handler;

import com.ct.myim.im.entity.SocketMsg;
import com.ct.myim.im.service.MsgService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EditLookMsgHandler {

    @Resource
    private MsgService service;

    public void edit(SocketMsg sockeMsg){
        service.editLookMsgRecord(sockeMsg.getFormUserName(),sockeMsg.getToContactUserName());
    }
}
