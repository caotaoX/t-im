package com.ct.myim.im.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(value = "sys_user" )
public class User {

    /**
     * 用户主键 */
    @Id
    private String id;
    /** 用户账号 */
    private String userName;
    /** 用户昵称 */
    private String nickName;
    /** 用户通讯录索引 */
    private String index;

    /**
     * 是否是群
     */
    private boolean isGroup = false;

    /**
     * 管理员组
     */
    private String root;

    /** 用户性别（0男 1女 2未知） */
    private String sex;
    /** 头像路径 */
    private String avatar;
    /** 密码 */
    private String password;
    /** 最后登陆IP */
    private String loginIp;
    /** 最后登陆位置 */
    private String loginLocation;
    /** 最后登陆时间 */
    private Date loginDate;

    /** 注册时间 */
    private Date registerDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getRoot() {
        return root;
    }
}
