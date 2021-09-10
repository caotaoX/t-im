package com.ct.myim.im.service;

import cn.hutool.core.lang.Dict;
import com.ct.myim.common.utils.IpUtils;
import com.ct.myim.common.utils.PasswordUtils;
import com.ct.myim.common.utils.ServletUtils;
import com.ct.myim.framework.redis.TokenService;
import com.ct.myim.framework.web.entity.AjaxResult;
import com.ct.myim.framework.web.entity.LoginUser;
import com.ct.myim.im.entity.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class LoginService {

    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private TokenService tokenService;
    @Resource
    private NoticeService noticeService;

    @Resource
    private UserService userService;

    public AjaxResult login(User user){
        User one = userService.getUserByuserName(user.getUserName());
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
