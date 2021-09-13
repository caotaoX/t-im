package com.ct.myim.im.cache;

import cn.hutool.core.util.ObjectUtil;
import com.ct.myim.common.constant.Constants;
import com.ct.myim.framework.redis.RedisCache;
import com.ct.myim.im.entity.ContactsUser;
import com.ct.myim.im.entity.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 好友列表缓存
 */
@Service
public class ContactsUserCache {

    @Resource
    private RedisCache redisCache;
    @Resource
    private MongoTemplate mongoTemplate;

    public void setContactsUserCache(String userName){
        refresh(userName);
    }

    public List<ContactsUser> getContactsUserCache(String userName){
        try {
            List<ContactsUser> list = redisCache.getCacheList(Constants.CONTACTS_USER_CACHE + userName);
            if(ObjectUtil.isEmpty(list)){
                list =  refresh(userName);
            }
            redisCache.expire(Constants.CONTACTS_USER_CACHE + userName,30, TimeUnit.MINUTES);
            return list;
        }catch (Exception e){
            e.printStackTrace();
            return getContactsUserList(userName);
        }
    }

    public void deleteContactsUserCache(String userName){
        try {
            redisCache.deleteObject(Constants.CONTACTS_USER_CACHE + userName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<ContactsUser> refresh(String userName){
        try {
            List<ContactsUser> userList = mongoTemplate.find(new Query(Criteria.where("userName").is(userName)), ContactsUser.class);
            redisCache.setCacheList(Constants.CONTACTS_USER_CACHE + userName,userList);
            redisCache.expire(Constants.CONTACTS_USER_CACHE + userName,30, TimeUnit.MINUTES);
            return userList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private List<ContactsUser> getContactsUserList(String userName){
        return  mongoTemplate.find(new Query(Criteria.where("userName").is(userName)), ContactsUser.class);
    }
 }
