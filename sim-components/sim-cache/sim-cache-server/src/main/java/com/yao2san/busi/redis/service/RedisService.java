package com.yao2san.busi.redis.service;

import com.yao2san.busi.redis.bean.CacheInstance;
import com.yao2san.sim.framework.web.response.ResponseData;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

public interface RedisService {
    /**
     * 连接redis
     *
     * @param redisProperties redis配置
     */
    ResponseData connect(RedisProperties redisProperties);

    ResponseData ping();

    /**
     * 查询redis数据库
     */
    ResponseData getDbs();

    /**
     * 查询redis基本信息
     */
    ResponseData info();

    /**
     * 执行命令
     */
    ResponseData command(String command);

    /**
     * redis监控指标
     */
    ResponseData metrics();

    ResponseData query(String dataType, String key, String field, Integer start, Integer end);


    ResponseData addInstance(CacheInstance cacheInstance);

    ResponseData qryInstance(CacheInstance cacheInstance);

    ResponseData updateInstance(CacheInstance cacheInstance);

    ResponseData deleteInstance(Long cacheInstanceId);
}
