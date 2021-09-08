package com.ct.myim.framework.interceptor;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSON;
import com.ct.myim.common.constant.Constants;
import com.ct.myim.common.utils.SpringUtils;
import com.ct.myim.common.utils.StringUtils;
import com.ct.myim.framework.redis.RedisCache;
import com.ct.myim.framework.redis.TokenService;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class LoginHandlerInterceptor implements HandlerInterceptor {

    RedisCache redisCache = SpringUtils.getBean(RedisCache.class);
    TokenService tokenService = SpringUtils.getBean(TokenService.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String token = request.getHeader("authorization");
            if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
                token = token.replace(Constants.TOKEN_PREFIX, "");
            }
            if (redisCache.exists(Constants.LOGIN_TOKEN_KEY + token)) {
                tokenService.verifyToken(token);
                return true;
            } else {
                Dict dict = new Dict();
                dict.set("code",401);
                dict.set("msg","请登录！");
                returnJson(response,dict);
                return false;
            }

        } catch (Exception e) {
            StaticLog.error("记录日志失败" + e.getMessage());
        }
        return false;
    }


    private void returnJson(HttpServletResponse response, Dict dict) {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(dict);
            writer.flush();
        } catch (IOException e) {
            StaticLog.error(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
