export default {
    HTTP_IP_PROT: process.env.VUE_APP_BASE_API,
    SOCKTE_IP_PROT: process.env.VUE_APP_BASE_SOCKET,
    //私聊
    PRIVATE_CHAT: 1,
    // 群聊
    GROUP_CHAT: 2,
    // 获取联系人列表
    CONTACTS: 3,
    // 是否为群消息（是）
    GRUOP_YES: 6,
    // 是否为群消息（否）
    GRUOP_NO: 7,
    // 修改联系人信息
    UPDATE_USER: 8,
    // 通知新增联系人
    NOTICE_ADD_USER: 11,
    // 通知删除联系人
    NOTICE_DELETE_USER: 12,
    // 通知退出登录
    NOTICE_OUT_LOGIN: 13,

    // 通知删除消息
    OTICE_DELETE_MSG: 14,
    // 通知撤销消息
    NOTICE_REVOKE_MSG: 15,
    // 修改消息查看标识
    EDIT_LOOK_MSG_RECORD: 16,
     // 发送视频信息
    SEND_VIDE_CALL: 17,
    // 发送视频请求
    SEND_VIDE_CALL_YES: 18,
    // 发送共享屏幕请求
    RECORDING_SCREEN_YES: 19,
    // 发送视频信息
    SEND_RECORDING_SCREEN: 20

}