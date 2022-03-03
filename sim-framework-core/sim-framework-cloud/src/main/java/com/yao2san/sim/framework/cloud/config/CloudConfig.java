package com.yao2san.sim.framework.cloud.config;

import com.alibaba.cloud.nacos.NacosConfigAutoConfiguration;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.yao2san.sim.framework.cloud.exception.NacosConfigNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author wxg
 * Nacos configuration acquisition tool
 */
@Component
@ConditionalOnClass(NacosConfigAutoConfiguration.class)
@ConditionalOnProperty(name = "spring.cloud.nacos.config.enabled", havingValue = "true", matchIfMissing = true)
public class CloudConfig {
    private static final Logger logger = LoggerFactory.getLogger(CloudConfig.class);

    private static String serverAddr;
    private static String namespace;
    private static String active;

    private static String[] groups;

    private static String defaultDataId = "appconfig";

    private static volatile Properties allConfig;

    @Value("${spring.cloud.nacos.config.server-addr}")
    private void setServerAddr(String serverAddr) {
        CloudConfig.serverAddr = serverAddr;
    }

    @Value("${spring.cloud.nacos.config.namespace}")
    private void setNamespace(String namespace) {
        CloudConfig.namespace = namespace;
    }

    @Value("${spring.cloud.nacos.config.groups}")
    private void setGroups(String[] groups) {
        CloudConfig.groups = groups;
    }

    @Value("${spring.profiles.active}")
    private void setActive(String active) {
        CloudConfig.active = active;
    }

    public static String getValue(String key) {
        return getValueOrDefault(key, null);
    }

    public static String getValueOrDefault(String key, String defaultValue) {
        if (allConfig == null) {
            Properties config = getAll(true);
            return config.getProperty(key, defaultValue);
        }
        return allConfig.getProperty(key, defaultValue);
    }

    @PostConstruct
    public Properties getAll() {
        return getAll(false);
    }

    private static Properties getAll(boolean isReload) {
        if (allConfig != null && !isReload) {
            return allConfig;
        } else if (allConfig != null && isReload) {
            allConfig.clear();
        }
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        properties.put("namespace", namespace);
        try {
            ConfigService configService = NacosFactory.createConfigService(properties);
            if (allConfig == null) {
                synchronized (CloudConfig.class) {
                    if (allConfig == null) {
                        allConfig = new Properties();
                    }
                }
            }
            if (groups != null && groups.length > 0) {
                logger.info("Loading group configs:{}", String.join(",", groups));
                for (String group : groups) {
                    String dataId = defaultDataId + "-" + active + ".properties";
                    String config = configService.getConfig(dataId, group, 3000);
                    if (config == null) {
                        String expMsg = String.format("No configuration file found.group:%s,dataId:%s", group, dataId);
                        throw new NacosConfigNotFoundException(expMsg);
                    }
                    allConfig.load(new StringReader(config));
                }
            }
        } catch (NacosException | IOException e) {
            logger.error("create nacos config service error.", e);
        }
        return allConfig;
    }

    @PostConstruct
    private void setListener() {
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        properties.put("namespace", namespace);
        ConfigService configService;
        try {
            configService = NacosFactory.createConfigService(properties);
            if (groups != null && groups.length > 0) {
                logger.info("add config listener,group:{}", String.join(",", groups));
                for (String group : groups) {
                    String dataId = defaultDataId + "-" + active + ".properties";
                    configService.addListener(dataId, group, new Listener() {
                        @Override
                        public Executor getExecutor() {
                            return null;
                        }

                        @Override
                        public void receiveConfigInfo(String s) {
                            logger.info("receive config change,reload all configs.");
                            getAll(true);
                        }
                    });

                }
            }
        } catch (NacosException e) {
            logger.error("create nacos config service error.", e);
        }
    }
}
