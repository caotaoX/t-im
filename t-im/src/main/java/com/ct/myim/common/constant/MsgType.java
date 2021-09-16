package com.ct.myim.common.constant;

/**
 * 消息类型
 */
public class MsgType {
    /**
     * 私聊
     */
    public static final int PRIVATE_CHAT = 1;

    /**
     * 群聊
     */
    public static final int GROUP_CHAT = 2;


    /**
     *获取联系人列表
     */
    public static final int CONTACTS = 3;

    /**
     * 消息状态（已发送）
     */
    public static final int SEND_OK = 4;

    /**
     * 消息状态（未发送）
     */
    public static final int SEND_NO = 5;

    /**
     * 消息删除状态（正常）
     */
    public static final int DELETE_NO = 6;

    /**
     * 消息删除状态（删除）
     */
    public static final int DELETE_YES = 7;


    /**
     *  修改联系人信息
     */
    public static final int UPDATE_USER = 8;

    /**
     *  添加联系人状态（完成）
     */
    public static final int ADD_FRIEND_YES = 9;

    /**
     *  添加联系人状态（未完成）
     */
    public static final int ADD_FRIEND_NO = 10;

    /**
     * 通知新增联系人
     */
    public static final int NOTICE_ADD_USER = 11;

    /**
     * 通知删除联系人
     */
    public static final int NOTICE_DELETE_USER = 12;

    /**
     * 通知退出登录
     */
    public static final int NOTICE_OUT_LOGIN = 13;

    /**
     * 通知删除消息
     */
    public static final int NOTICE_DELETE_MSG = 14;

    /**
     * 通知撤销消息
     */
    public static final int NOTICE_REVOKE_MSG = 15;

    /**
     * 修改消息查看标识
     */
    public static final int EDIT_LOOK_MSG_RECORD = 16;

    /**
     * 发送视频信息
     */
    public static final int SEND_VIDE_CALL = 17;

    /**
     * 发送视频请求
     */
    public static final int SEND_VIDE_CALL_YES = 18;

    /**
     * 发送共享屏幕请求
     */
    public static final int RECORDING_SCREEN_YES = 19;

    /**
     * 发送视频信息
     */
    public static final int SEND_RECORDING_SCREEN = 20;




}
