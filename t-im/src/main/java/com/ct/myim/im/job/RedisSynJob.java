package com.ct.myim.im.job;

import com.ct.myim.common.constant.Constants;
import com.ct.myim.common.utils.IdUtils;
import com.ct.myim.framework.redis.RedisCache;
import com.ct.myim.im.entity.MsgLookCalipers;
import com.ct.myim.sockent.manager.WsClientManager;
import io.netty.channel.Channel;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisSynJob {

    @Resource
    private RedisCache redisCache;

    @Resource
    public RedisTemplate redisTemplate;

    @Resource
    private MongoTemplate mongoTemplate;


    @Scheduled(cron = "0 0 2 * * ?")
    public void synMsgLookCalipers() {
        redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match("*" + Constants.MSG_LOOK_CALIPERS_CACHE + "*").count(Long.MAX_VALUE).build());
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                MsgLookCalipers calipers = redisCache.getCacheObject(key);
                if (calipers.getTime() != 0 && calipers.getId() != null) {
                    Update update = new Update();
                    update.set("time", calipers.getTime());
                    mongoTemplate.updateFirst(new Query(Criteria.where("id").is(calipers.getId())), update, MsgLookCalipers.class);
                } else {
                    calipers.setId(IdUtils.fastSimpleUUID());
                    mongoTemplate.insert(calipers);
                }
                Channel channel = WsClientManager.getInstance().getChannel(calipers.getUserName());
                if(channel == null){
                    redisCache.expire(key,1, TimeUnit.MINUTES);
                }
            }
            return null;
        });
    }

}
