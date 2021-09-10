package com.ct.myim.im.cache;

import com.ct.myim.common.constant.Constants;
import com.ct.myim.framework.redis.RedisCache;
import com.ct.myim.im.entity.MsgDeleteCalipers;
import com.ct.myim.im.entity.MsgLookCalipers;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

/**
 * 消息查看缓存
 */
@Service
public class MsgLookCalipersCache {


    @Resource
    private RedisCache redisCache;
    @Resource
    private MongoTemplate mongoTemplate;

    public void setMsgLookCalipersCache(String fromId,String toId){
        MsgLookCalipers msgLookCalipers = refresh(fromId,toId);
        if(msgLookCalipers.getTime() == 0){
            msgLookCalipers.setUserName(fromId);
            msgLookCalipers.setContacts(toId);
            msgLookCalipers.setTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            redisCache.setCacheObject(Constants.MSG_LOOK_CALIPERS_CACHE + fromId + ":" + toId, msgLookCalipers);
        } else {
            msgLookCalipers.setTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            redisCache.setCacheObject(Constants.MSG_LOOK_CALIPERS_CACHE + fromId + ":" + toId, msgLookCalipers);
        }

    }

    public MsgLookCalipers getMsgLookCalipersCache(String fromId, String toId){
        MsgLookCalipers msgLookCalipers = redisCache.getCacheObject(Constants.MSG_LOOK_CALIPERS_CACHE + fromId + ":" + toId);
        if(msgLookCalipers == null){
            msgLookCalipers = refresh(fromId,toId);
        }
        if(msgLookCalipers.getTime() == 0){
            return null;
        }
        return msgLookCalipers;
    }

    public MsgLookCalipers refresh(String fromId, String toId){
        try {
            MsgLookCalipers calipers = mongoTemplate.findOne(new Query(Criteria.where("userName").is(fromId)
                    .and("contacts").is(toId)), MsgLookCalipers.class);
            if(calipers == null){
                calipers = new MsgLookCalipers();
                calipers.setTime(0);
            }
            redisCache.setCacheObject(Constants.MSG_LOOK_CALIPERS_CACHE + fromId + ":" + toId, calipers);
            return calipers;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
