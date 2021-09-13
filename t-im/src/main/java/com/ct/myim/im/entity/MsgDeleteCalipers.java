package com.ct.myim.im.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 漫游消息删除标记
 */
@Document(value = "im_msg_delete_calipers" )
public class MsgDeleteCalipers {

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
