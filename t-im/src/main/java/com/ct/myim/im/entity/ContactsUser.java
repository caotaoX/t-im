package com.ct.myim.im.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 *联系人表
 */
@Document(value = "sys_contacts_user" )
public class ContactsUser {
    /**
     *用户账号
     */
    private String userName;

    /**
     * 用户联系人账号
     */
    private String contactsUserName;

    /**
     * 用户联系人备注、标签
     */
    private String label;

    /**
     * 成为好友时间
     */
    private Date time;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContactsUserName() {
        return contactsUserName;
    }

    public void setContactsUserName(String contactsUserName) {
        this.contactsUserName = contactsUserName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
