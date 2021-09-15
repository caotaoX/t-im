package com.ct.myim.im.handler;

import com.ct.myim.im.cache.MsgLookCalipersCache;
import com.ct.myim.im.entity.SocketMsg;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class EditLookMsgHandler {

    @Resource
    private MsgLookCalipersCache msgLookCalipersCache;

    public void edit(SocketMsg sockeMsg){
        msgLookCalipersCache.setMsgLookCalipersCache(sockeMsg.getFormUserName(),sockeMsg.getToContactUserName());
    }
}
