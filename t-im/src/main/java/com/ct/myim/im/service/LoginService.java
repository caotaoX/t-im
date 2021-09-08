package com.ct.myim.im.service;

import cn.hutool.core.lang.Dict;
import com.ct.myim.common.utils.IpUtils;
import com.ct.myim.common.utils.PasswordUtils;
import com.ct.myim.common.utils.ServletUtils;
import com.ct.myim.framework.redis.TokenService;
import com.ct.myim.framework.web.entity.AjaxResult;
import com.ct.myim.framework.web.entity.LoginUser;
import com.ct.myim.im.entity.User;
import com.ct.myim.sockent.manager.WsClientManager;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoginService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private NoticeService noticeService;

    public AjaxResult login(User user){
        User one = mongoTemplate.findOne(new Query(Criteria.where("userName").is(user.getUserName())), User.class);
        if(one == null || one.getUserName().equals("friendLog") || one.isGroup()){
            return AjaxResult.error("账号不存在！");
        }
        if(!PasswordUtils.decryptStrPwd(one.getPassword()).equals(user.getPassword())){
            return AjaxResult.error("密码错误！");
        }
        noticeService.outLogin(one.getUserName());
        one.setLoginIp(IpUtils.getHostIp());
//        one.setLoginLocation(IpUtils.getCityInfo(one.getLoginIp()));
        Update update = new Update();
        update.set("loginIp",one.getLoginIp());
        update.set("loginLocation",one.getLoginLocation());
        update.set("loginDate",new Date());
        mongoTemplate.upsert(new Query(Criteria.where("id").is(user.getId())),update,User.class);
        one.setPassword("");
        one.setAvatar("");
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(one);
        loginUser.setIpaddr(IpUtils.getHostIp());
//        loginUser.setLoginLocation(IpUtils.getCityInfo(loginUser.getIpaddr()));
        String token = tokenService.createToken(loginUser);
        Dict dict = new Dict();
        dict.set("token",token);
        return AjaxResult.success(dict);

    }

    public AjaxResult outLogin(){
        tokenService.delLoginUser(tokenService.getToken(ServletUtils.getRequest()));
        return AjaxResult.success();
    }
}
