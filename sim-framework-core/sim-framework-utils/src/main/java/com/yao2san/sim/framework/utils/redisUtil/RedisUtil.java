package com.yao2san.sim.framework.utils.redisUtil;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    public final static String TOKEN_KEY_PREFIX = "SIM:OAUTH:TOKEN:";
    private static RedisTemplate<String, String> client;

    @Autowired
    public void setClient(RedisTemplate client) throws Exception {
        RedisUtil.client = client;
    }

    public static String get(String key) {
        return client.opsForValue().get(key);
    }
    public static <T> T get(String key,Class<T> clzz) {
        String s = client.opsForValue().get(key);
        return JSONObject.parseObject(s, clzz);
    }

    public static <T> T hget(String hash, String key) {
        HashOperations<String, String, T> forHash = client.opsForHash();
        return forHash.get(hash, key);
    }

    public static void set(String key, String value) {
        client.opsForValue().set(key, value);
    }
    public static void set(String key, String value, long time, TimeUnit timeUnit) {
        client.opsForValue().set(key, value,time,timeUnit);
    }
    public static void hset(String hash, String key, String value) {
        client.opsForHash().put(hash, key, value);
    }

    public static Boolean del(String key){
        return client.delete(key);
    }

}
