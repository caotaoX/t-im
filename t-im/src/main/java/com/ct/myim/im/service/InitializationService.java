package com.ct.myim.im.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.ct.myim.common.constant.Constants;
import com.ct.myim.common.utils.IdUtils;
import com.ct.myim.common.utils.PasswordUtils;
import com.ct.myim.im.cache.ContactsUserCache;
import com.ct.myim.im.entity.ContactsUser;
import com.ct.myim.im.entity.SocketFile;
import com.ct.myim.im.entity.User;
import com.ct.myim.sockent.StartServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

@Service
public class InitializationService {

    @Value("${file.upload.abspath.prefix}")
    private String fileUploadPath;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private StartServer startServer;

    @Resource
    private UserService userService;


    @PostConstruct
    public void startWebSocket() {
        try {
            startServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void initializationAdminUserOrGroup(){
        User user = userService.getUserByuserName("admin");
        if(user == null){
            User user1 = new User();
            user1.setId(IdUtils.fastSimpleUUID());
            user1.setRegisterDate(new Date());
            user1.setPassword(PasswordUtils.encryptPwd("admin@2021.!"));
            user1.setAvatar(Constants.SYSTEM_ID);
            user1.setUserName("admin");
            user1.setNickName("系统通知");
            user1.setIndex("[3]系统通知");
            mongoTemplate.insert(user1);
        }
        User friendLog = userService.getUserByuserName("friendLog");
        if(friendLog == null){
            User friend = new User();
            friend.setId(IdUtils.fastSimpleUUID());
            friend.setRegisterDate(new Date());
            friend.setAvatar(Constants.FRIEND_AVATAR_ID);
            friend.setUserName("friendLog");
            friend.setNickName("新的朋友");
            friend.setGroup(true);
            friend.setIndex("[1]新的朋友");
            mongoTemplate.insert(friend);
        }
        User group1 = userService.getUserByuserName("admingroup");
        if(group1 == null){
            User group = new User();
            group.setId(IdUtils.fastSimpleUUID());
            group.setRegisterDate(new Date());
            group.setAvatar(Constants.GROUP_AVATAR_ID);
            group.setUserName("admingroup");
            group.setNickName("交流群");
            group.setGroup(true);
            group.setIndex("[2]群组");
            mongoTemplate.insert(group);
        }
        ContactsUser contactsUser = mongoTemplate.findOne(new Query(Criteria.where("userName").is("admin").and("contactsUserName").is("admingroup")), ContactsUser.class);
        if(contactsUser == null){
            ContactsUser contactsUser1 = new ContactsUser();
            contactsUser1.setUserName("admin");
            contactsUser1.setContactsUserName("admingroup");
            contactsUser1.setTime(new Date());
            mongoTemplate.insert(contactsUser1);
        }
        ContactsUser contactsUser2 = mongoTemplate.findOne(new Query(Criteria.where("userName").is("admingroup").and("contactsUserName").is("admin")), ContactsUser.class);
        if(contactsUser2 == null){
            ContactsUser contactsUser3 = new ContactsUser();
            contactsUser3.setUserName("admingroup");
            contactsUser3.setContactsUserName("admin");
            contactsUser3.setTime(new Date());
            mongoTemplate.insert(contactsUser3);
        }

        ContactsUser contactsUser3 = mongoTemplate.findOne(new Query(Criteria.where("userName").is("admin").and("contactsUserName").is("friendLog")), ContactsUser.class);
        if(contactsUser3 == null){
            ContactsUser contactsUser4 = new ContactsUser();
            contactsUser4.setUserName("admin");
            contactsUser4.setContactsUserName("friendLog");
            contactsUser4.setTime(new Date());
            mongoTemplate.insert(contactsUser4);
        }
    }

}
