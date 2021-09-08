package com.ct.myim.im.entity;


import com.ct.myim.common.constant.MsgType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *网络请求消息对象
 */
@Document(value = "socket_msg" )
public class SocketMsg {

    @Id
    private String id;

    /**
     * 消息唯一ID
     */
    private String msgId;

    /**
     * 请求类型
     */
    private int httpType;

    /**
     * 消息发送人
     */
    private String formUserName;


    /**
     * 消息接受人
     */
    private String toContactUserName;


    /**
     * 消息状态
     */
    private int msgType;

    /**
     * 消息删除状态
     */
    private int deleteType = MsgType.DELETE_NO;


    /**
     * 消息产生时间
     */
    private long sendTime;

    /**
     * 消息内容
     */
    private String  content;


    /**
     *  消息内容
     */
    private SocketMessage message;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getHttpType() {
        return httpType;
    }

    public void setHttpType(int httpType) {
        this.httpType = httpType;
    }

    public String getFormUserName() {
        return formUserName;
    }

    public void setFormUserName(String formUserName) {
        this.formUserName = formUserName;
    }

    public String getToContactUserName() {
        return toContactUserName;
    }

    public void setToContactUserName(String toContactUserName) {
        this.toContactUserName = toContactUserName;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getDeleteType() {
        return deleteType;
    }

    public void setDeleteType(int deleteType) {
        this.deleteType = deleteType;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SocketMessage getMessage() {
        return message;
    }

    public void setMessage(SocketMessage message) {
        this.message = message;
    }
}
