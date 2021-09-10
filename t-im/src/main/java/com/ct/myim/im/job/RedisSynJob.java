package com.ct.myim.im.job;

import com.ct.myim.common.constant.Constants;
import com.ct.myim.framework.redis.RedisCache;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

@Component
public class RedisSynJob {

    @Resource
    private RedisCache redisCache;

    @Resource
    public RedisTemplate redisTemplate;

    @PostConstruct
    public void test(){
        scan(Constants.USER_CACHE);
    }

    public void scan(String matchKey) {
        redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match("*" + matchKey + "*").count(Long.MAX_VALUE).build());
            while (cursor.hasNext()) {
                System.out.println(new String(cursor.next()));
            }
            return null;
        });

    }

}
