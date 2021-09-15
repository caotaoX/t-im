package com.ct.myim.im.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 消息查看标记
 */
@Document(value = "msg_look_calipers" )
public class MsgLookCalipers {

    @Id
    private String id;

    /**
     * 用户
     */
    private String userName;

    /**
     * 要删除消息的组或群
     */
    private String contacts;

    /**
     * 时间毫秒数
     */
    private long time;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
