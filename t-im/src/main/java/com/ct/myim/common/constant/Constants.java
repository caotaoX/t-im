package com.ct.myim.common.constant;



/**
 * 通用常量信息
 * 
 * @author CAOTAO
 */
public class Constants
{

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "im_tokens:";

    /**
     * 用户缓存
     */
    public static final String USER_CACHE = "user_cache:";

    /**
     * 用户联系人缓存
     */
    public static final String CONTACTS_USER_CACHE = "contacts_user_cache:";

    /**
     * 用户删除漫游消息缓存
     */
    public static final String MSG_DELETE_CALIPERS_CACHE = "msg_delete_calipers_cache:";

    /**
     * 用户查看消息缓存
     */
    public static final String MSG_LOOK_CALIPERS_CACHE = "msg_look_calipers_cache:";


    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String AVATAR_ID= "user";

    public static final String SYSTEM_ID= "system";

    public static final String  GROUP_AVATAR_ID= "group";

    public static final String  FRIEND_AVATAR_ID= "friend";
}
