package com.yao2san.busi.redis.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yao2san.busi.redis.bean.CacheInstance;
import com.yao2san.busi.redis.service.RedisService;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import com.yao2san.utils.RedisCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionCommands;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class RedisServiceImpl extends BaseServiceImpl implements RedisService {
    private RedisTemplate<String, String> redisTemplate;

    private final static String CLUSTER = "CLUSTER";
    private final static String STANDALONE = "STANDALONE";

    @Override
    public ResponseData connect(RedisProperties redisProperties) {
        try {
            this.releaseConnection();
            this.initRedisTemplate(redisProperties);
            //ping
            String pong = redisTemplate.execute(RedisConnectionCommands::ping);
            if (StringUtils.equalsIgnoreCase(pong, "pong")) {
                return ResponseData.success("连接成功");
            } else {
                return ResponseData.businessError("连接失败");
            }
        } catch (Exception e) {
            log.error("redis连接失败:", e);
            return ResponseData.businessError("连接失败:" + e.getMessage());
        }


    }

    @Override
    public ResponseData ping() {
        try {
            String pong = redisTemplate.execute((RedisCallback<String>) connection -> connection.ping());
            return ResponseData.success(pong);
        } catch (Exception e) {
            return ResponseData.businessError(e.getMessage());
        }

    }

    @Override
    public ResponseData getDbs() {
        Object databases = redisTemplate.execute((RedisCallback<Object>) redisConnection -> redisConnection.getConfig("databases"));
        return ResponseData.success(databases);
    }

    @Override
    public ResponseData info() {
        Map<String, Object> infoMap = new HashMap<>();
        Object serverInfo = redisTemplate.execute((RedisCallback<Object>) redisConnection -> redisConnection.info("server"));
        Object clientInfo = redisTemplate.execute((RedisCallback<Object>) redisConnection -> redisConnection.info("clients"));
        Object memoryInfo = redisTemplate.execute((RedisCallback<Object>) redisConnection -> redisConnection.info("memory"));
        Object persistenceInfo = redisTemplate.execute((RedisCallback<Object>) redisConnection -> redisConnection.info("persistence"));
        Object statsInfo = redisTemplate.execute((RedisCallback<Object>) redisConnection -> redisConnection.info("stats"));
        Object replicationInfo = redisTemplate.execute((RedisCallback<Object>) redisConnection -> redisConnection.info("replication"));
        Object cpuInfo = redisTemplate.execute((RedisCallback<Object>) redisConnection -> redisConnection.info("cpu"));
        Object clusterInfo = redisTemplate.execute((RedisCallback<Object>) redisConnection -> redisConnection.info("cluster"));
        Object keyspaceInfo = redisTemplate.execute((RedisCallback<Object>) redisConnection -> redisConnection.info("keyspace"));

        infoMap.put("server", serverInfo);
        infoMap.put("clients", clientInfo);
        infoMap.put("memory", memoryInfo);
        infoMap.put("persistence", persistenceInfo);
        infoMap.put("stats", statsInfo);
        infoMap.put("replication", replicationInfo);
        infoMap.put("cpu", cpuInfo);
        infoMap.put("cluster", clusterInfo);
        infoMap.put("keyspace", keyspaceInfo);

        return ResponseData.success(infoMap);
    }

    @Override
    public ResponseData command(String command) {
        String checkRes = this.checkCmd(command);
        if (StringUtils.isNotEmpty(checkRes)) {
            return ResponseData.success(checkRes);
        }
        Object result;
        try {
            if (StringUtils.equals(command.split(" ")[0], "scan")) {
                Set<String> results = this.scanAll(command.split(" ")[1]);
                return ResponseData.success(results);
            }
            result = redisTemplate.execute((RedisCallback<Object>) redisConnection -> {
                DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
                String cmd = RedisCommand.getCommandSting(command);
                redisScript.setScriptText(cmd);
                redisScript.setResultType(String.class);
                log.info("script:{}", cmd);
                return redisTemplate.execute(redisScript, RedisCommand.getKeysAndArgs(command).getKeys(), RedisCommand.getKeysAndArgs(command).getArgs().toArray());
            });
        } catch (Exception e) {
            log.error("", e);
            return ResponseData.success(Collections.singletonMap("error", e.getMessage()));
        }
        return ResponseData.success(result);
    }

    private String checkCmd(String command) {
        return "";
    }

    private Set<String> scanAll(String pattern) {
        List execList;
        Set<String> keys = new HashSet();

        String count = "3000";
        String cursor = "0";

        RedisScript<List> redisScript = RedisScript.of(
                "return redis.call('scan',KEYS[1],'MATCH',ARGV[1],'count',ARGV[2])", List.class);
        RedisSerializer serializer = redisTemplate.getStringSerializer();
        do {
            execList = redisTemplate.execute(redisScript, serializer,
                    serializer, Collections.singletonList(cursor), pattern, count);
            assert execList != null;

            cursor = String.valueOf(execList.get(0));
            keys.addAll((List<String>) execList.get(1));
        } while (!"0".equals(cursor));
        return keys;
    }


    @Override
    public ResponseData metrics() {
        return null;
    }

    @Override
    public ResponseData query(String dataType, String key, String field, Integer start, Integer end) {
        if (this.redisTemplate == null) {
            return ResponseData.businessError("redis未连接");
        }
        dataType = dataType.toLowerCase();
        Map<String, Object> result = new HashMap<>();

        switch (dataType) {
            case "string":
                result.put("value", this.redisTemplate.opsForValue().get(key));
                result.put("expire", this.redisTemplate.opsForValue().getOperations().getExpire(key));
                break;
            case "hash":
                if (StringUtils.isEmpty(field)) {
                    result.put("value", JSONObject.toJSONString(this.redisTemplate.opsForHash().entries(key)));
                    result.put("expire", this.redisTemplate.opsForHash().getOperations().getExpire(key));
                } else {
                    result.put("value", this.redisTemplate.opsForHash().get(key, field));
                    result.put("expire", this.redisTemplate.opsForHash().getOperations().getExpire(key));
                }
                break;

            case "list":
                result.put("value", this.redisTemplate.opsForList().range(key, start, end));
                result.put("expire", this.redisTemplate.opsForList().getOperations().getExpire(key));
                break;
            case "set":
                result.put("value", this.redisTemplate.opsForSet().members(key));
                result.put("expire", this.redisTemplate.opsForSet().getOperations().getExpire(key));
                break;
            case "zset":
                result.put("value", this.redisTemplate.opsForZSet().range(key, start, end));
                result.put("expire", this.redisTemplate.opsForZSet().getOperations().getExpire(key));
                break;
        }
        return ResponseData.success(result);
    }

    @Override
    public ResponseData addInstance(CacheInstance cacheInstance) {
        sqlSession.insert("redis.addCacheInstance", cacheInstance);
        return ResponseData.success();
    }

    @Override
    public ResponseData qryInstance(CacheInstance cacheInstance) {
        List<Object> list = sqlSession.selectList("redis.qryCacheInstance", cacheInstance);
        return ResponseData.success(list);
    }

    @Override
    public ResponseData updateInstance(CacheInstance cacheInstance) {
        sqlSession.update("redis.updateCacheInstance", cacheInstance);
        return ResponseData.success();
    }

    @Override
    public ResponseData deleteInstance(Long cacheInstanceId) {
        sqlSession.delete("redis.deleteInstance", cacheInstanceId);
        return ResponseData.success();
    }

    private void releaseConnection() {
        if (redisTemplate != null) {
            if (redisTemplate.getConnectionFactory() != null)
                RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
    }

    private void initRedisTemplate(RedisProperties redisProperties) {
        LettuceConnectionFactory factory;
        if (redisProperties.getCluster() != null) {
            log.info("redis集群连接");
            RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(redisProperties.getCluster().getNodes());
            LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder();
            factory = new LettuceConnectionFactory(
                    redisClusterConfiguration,
                    lettuceClientConfigurationBuilder.build());
        } else {
            log.info("redis单机连接");
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setHostName(redisProperties.getHost());
            redisStandaloneConfiguration.setPort(redisProperties.getPort());
            redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
            LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder();

            factory = new LettuceConnectionFactory(
                    redisStandaloneConfiguration,
                    lettuceClientConfigurationBuilder.build());
        }

        factory.afterPropertiesSet();
        redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.afterPropertiesSet();

    }
}