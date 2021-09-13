package com.ct.myim.im.cache;

import com.ct.myim.common.constant.Constants;
import com.ct.myim.framework.redis.RedisCache;
import com.ct.myim.im.entity.ContactsUser;
import com.ct.myim.im.entity.MsgDeleteCalipers;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 漫游消息缓存
 */
@Service
public class MsgDeleteCalipersCache {

    @Resource
    private RedisCache redisCache;
    @Resource
    private MongoTemplate mongoTemplate;

    public void setMsgDeleteCalipersCache(String fromId,String toId){
        refresh(fromId,toId);
    }

    public MsgDeleteCalipers getMsgDeleteCalipersCache(String fromId,String toId){
        try {
            MsgDeleteCalipers msgDeleteCalipers = redisCache.getCacheObject(Constants.MSG_DELETE_CALIPERS_CACHE + fromId + ":" + toId);
            if(msgDeleteCalipers == null){
                msgDeleteCalipers = refresh(fromId,toId);
            }
            redisCache.expire(Constants.MSG_DELETE_CALIPERS_CACHE + fromId + ":" + toId,30,TimeUnit.MINUTES);
            if(msgDeleteCalipers.getTime() == 0){
                return null;
            }
            return msgDeleteCalipers;
        }catch (Exception e){
            e.printStackTrace();
            return getMsgDeleteCalipers(fromId,toId);
        }
    }

    public MsgDeleteCalipers refresh(String fromId, String toId){
        try {
            MsgDeleteCalipers calipers = mongoTemplate.findOne(new Query(Criteria.where("userName").is(fromId)
                    .and("contacts").is(toId)), MsgDeleteCalipers.class);
            if(calipers == null){
                calipers = new MsgDeleteCalipers();
                calipers.setTime(0);
            }
            redisCache.setCacheObject(Constants.MSG_DELETE_CALIPERS_CACHE + fromId + ":" + toId, calipers,30, TimeUnit.MINUTES);
            return calipers;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private MsgDeleteCalipers getMsgDeleteCalipers(String fromId, String toId){
        return  mongoTemplate.findOne(new Query(Criteria.where("userName").is(fromId)
                .and("contacts").is(toId)), MsgDeleteCalipers.class);
    }

}
