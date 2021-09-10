package com.ct.myim.im.cache;

import com.ct.myim.common.constant.Constants;
import com.ct.myim.framework.redis.RedisCache;
import com.ct.myim.im.entity.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class UserCache {
    @Resource
    private RedisCache redisCache;
    @Resource
    private MongoTemplate mongoTemplate;

    public void setUserCache(User user){
        try {
            redisCache.setCacheObject(Constants.USER_CACHE + user.getUserName(),user, 30, TimeUnit.MINUTES);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public User getUserCache(String userName){
        try {
            User user =  redisCache.getCacheObject(Constants.USER_CACHE + userName);
            if(user == null){
                user = mongoTemplate.findOne(new Query(Criteria.where("userName").is(userName)), User.class);
                setUserCache(user);
            }
            redisCache.expire(Constants.USER_CACHE + userName,30, TimeUnit.MINUTES);
            return user;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void deleteUserCache(String userName){
        try {
            redisCache.deleteObject(Constants.USER_CACHE + userName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
