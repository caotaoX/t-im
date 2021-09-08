package com.ct.myim.common.constant;


import com.ct.myim.common.utils.IdUtils;

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
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String AVATAR_ID= "user";

    public static final String SYSTEM_ID= "system";

    public static final String  GROUP_AVATAR_ID= "group";

    public static final String  FRIEND_AVATAR_ID= "friend";
}
