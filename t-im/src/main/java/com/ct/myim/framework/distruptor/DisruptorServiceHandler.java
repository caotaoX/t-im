package com.ct.myim.framework.distruptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ct.myim.common.constant.MsgType;
import com.ct.myim.common.utils.SpringUtils;
import com.ct.myim.framework.distruptor.base.BaseEvent;
import com.ct.myim.im.entity.FromUser;
import com.ct.myim.im.entity.SocketMessage;
import com.ct.myim.im.entity.SocketMsg;
import com.ct.myim.im.handler.GroupMsgHandler;
import com.ct.myim.im.handler.PersonalMsgHandler;
import com.lmax.disruptor.WorkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 队列任务分配器
 * @author CAOTAO
 */
public class DisruptorServiceHandler implements WorkHandler<BaseEvent> {


    private Logger logger = LoggerFactory.getLogger(DisruptorServiceHandler.class);


    private PersonalMsgHandler personalMsgHandler = SpringUtils.getBean(PersonalMsgHandler.class);


    private GroupMsgHandler groupMsgHandler = SpringUtils.getBean(GroupMsgHandler.class);

    @Override
    public void onEvent(BaseEvent baseEvent)  {
        try {
            JSONObject msg = JSON.parseObject(baseEvent.getMsg());
            SocketMsg sockeMsg = JSON.toJavaObject(msg, SocketMsg.class);
            JSONObject message = msg.getJSONObject("message");
            if(message != null){
                sockeMsg.setMessage(JSON.toJavaObject(message, SocketMessage.class));
                sockeMsg.getMessage().setFromUser(JSON.toJavaObject(message.getJSONObject("fromUser"), FromUser.class));
            }
            if(MsgType.PRIVATE_CHAT == sockeMsg.getHttpType()){
                personalMsgHandler.send(sockeMsg);
            }
            if(MsgType.GROUP_CHAT == sockeMsg.getHttpType()){
                groupMsgHandler.send(sockeMsg);
            }
        } catch (Exception e) {
            logger.error("Disruptor事件执行异常", e);
        }
    }

}
