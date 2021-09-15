package com.ct.myim.im.entity;

import com.ct.myim.common.constant.MsgType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(value = "socket_add_user" )
public class SocketAddUser {

    @Id
    private String id;

    /**
     * 是谁添加
     */
    private String fromUserName;

    /**
     * 添加谁
     */
    private String toUserName;

    /**
     * 添加说明
     */
    private String explain;

    /**
     * 添加时间
     */
    private Date time;

    /**
     * 状态
     */
    private int state = MsgType.ADD_FRIEND_NO;


    /**
     * 通过状态
     */
    private int type = MsgType.ADD_FRIEND_NO;

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
