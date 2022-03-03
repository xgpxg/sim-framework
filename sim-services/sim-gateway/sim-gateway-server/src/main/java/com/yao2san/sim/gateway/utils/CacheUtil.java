package com.yao2san.sim.gateway.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CacheUtil {
    private final static int CACHE_SIZE = 1000_00;
    private final static int CACHE_TIME = 1;
    private static final LoadingCache<String, Object> GLOBAL_CACHE;

    static {
        GLOBAL_CACHE = CacheBuilder.newBuilder()
                .maximumSize(CACHE_SIZE)
                .expireAfterAccess(CACHE_TIME, TimeUnit.DAYS)
                .expireAfterWrite(CACHE_TIME, TimeUnit.DAYS)
                .recordStats()
                .build(new CacheLoader<String, Object>() {
                    @Override
                    public Object load(String key) throws Exception {
                        return Optional.empty();
                    }
                });
    }

    public static void put(String key, Object value) {
        GLOBAL_CACHE.put(key, value);
    }

    public static Object get(String key) {
        return get(key, null);
    }

    public static Object get(String key, Object defaultValue) {
        try {
            Object value = GLOBAL_CACHE.get(key);
            return value == null ? defaultValue : value;
        } catch (ExecutionException e) {
            log.error("", e);
        }
        return null;
    }
}
