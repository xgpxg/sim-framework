package com.yao2san.busi.redis.controller;

import com.yao2san.busi.redis.bean.CacheInstance;
import com.yao2san.busi.redis.service.RedisService;
import com.yao2san.sim.framework.web.response.ResponseData;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class RedisController {

    @Autowired
    private RedisService redisService;

    /**
     * 连接redis
     *
     * @param redisProperties redis连接信息
     */
    @PostMapping("connect")
    public ResponseData connect(@RequestBody RedisProperties redisProperties) {
        return redisService.connect(redisProperties);
    }

    @GetMapping("ping")
    public ResponseData ping() {
        return redisService.ping();
    }

    /**
     * 获取redis数据库
     */
    @GetMapping("showDb")
    public ResponseData showDb() {
        return redisService.getDbs();
    }

    /**
     * redis信息
     */
    @GetMapping("info")
    public ResponseData info() {
        return redisService.info();
    }

    /**
     * 执行redis命令
     */
    @PostMapping("command")
    public ResponseData command(@RequestBody Map<String, Object> param) {
        return redisService.command(MapUtils.getString(param, "command"));
    }

    @GetMapping("query")
    public ResponseData query(String dataType, String key, String field, Integer start, Integer end) {
        return redisService.query(dataType, key, field, start, end);
    }

    @PostMapping("addInstance")
    public ResponseData addInstance(@RequestBody CacheInstance cacheInstance) {
        return redisService.addInstance(cacheInstance);
    }

    @GetMapping("qryInstance")
    public ResponseData qryInstance(CacheInstance cacheInstance) {
        return redisService.qryInstance(cacheInstance);
    }

    @PatchMapping("updateInstance")
    public ResponseData updateInstance(CacheInstance cacheInstance) {
        return redisService.updateInstance(cacheInstance);
    }

    @DeleteMapping("deleteInstance")
    public ResponseData deleteInstance(Long cacheInstanceId) {
        return redisService.deleteInstance(cacheInstanceId);
    }
}
