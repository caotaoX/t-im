package com.ct.myim.im.controller;

import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.ct.myim.common.constant.MsgType;
import com.ct.myim.framework.distruptor.base.BaseEvent;
import com.ct.myim.framework.distruptor.base.MessageProducer;
import com.ct.myim.framework.web.entity.AjaxResult;
import com.ct.myim.im.entity.SocketMsg;
import com.ct.myim.im.service.MsgService;
import com.ct.myim.im.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("msg")
public class MsgController {
    @Autowired
    private MsgService msgService;
    @Resource
    private MessageProducer messageProducer ;



    @GetMapping("list")
    public AjaxResult getMsgList(Integer pageSize,String fromID,String toId,boolean isGroup){
        return  msgService.getMsgList(pageSize,fromID,toId,isGroup);
    }

    @GetMapping("getContactsList")
    public AjaxResult getContactsList(String formUserName,String searchValue){
        return msgService.getContactsList(formUserName,searchValue);
    }

    @PostMapping("deleteRoamingRecord")
    public AjaxResult deleteRoamingRecord(String userName){
        return msgService.deleteRoamingRecord(userName);
    }

    @PostMapping("deleteMsg")
    public AjaxResult deleteMsg(String contactId,String msgId){
        return msgService.deleteMsg(contactId,msgId);
    }

    @PostMapping("revokeMsg")
    public AjaxResult revokeMsg(String contactId,String msgId){
        return msgService.revokeMsg(contactId,msgId);
    }


}
