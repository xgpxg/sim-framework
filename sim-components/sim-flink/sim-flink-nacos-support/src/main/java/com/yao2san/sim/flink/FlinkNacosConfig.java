package com.yao2san.sim.flink;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.listener.impl.PropertiesListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;

/**
 * flink nacos support
 *
 * @author wxg
 */
public class FlinkNacosConfig {
    private static final Logger logger = LoggerFactory.getLogger(FlinkNacosConfig.class);
    private static final String ENABLE_NACOS_KEY = "sim.flink.nacos.enable";
    private static final String SERVER_ADDR_KEY = "sim.flink.nacos.serverAddr";
    private static final String NAMESPACE_KEY = "sim.flink.nacos.namespace";
    private static final String GROUPS_KEY = "sim.flink.nacos.groups";
    private static final String DATA_IDS_KEY = "sim.flink.nacos.dataIds";
    private static final String TIMEOUT_KEY = "sim.flink.nacos.timeout";

    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    private static final String DEFAULT_DATA_ID = "appconfig.properties";
    private static final String DEFAULT_TIMEOUT = "3000";

    private static String SERVER_ADDR;
    private static String NAMESPACE;
    private static String[] GROUPS;
    private static String[] DATA_IDS;
    private static Integer TIMEOUT;

    private static Properties CONFIG_MAP = new Properties();
    private static Properties NACOS_CONFIG_MAP = new Properties();

    private static final String DEFAULT_CONFIG_FILE = "application.properties";

    static {
        InputStream inputStream = FlinkNacosConfig.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE);
        if (inputStream == null) {
            logger.info("no flink nacos config found");
        } else {
            try {
                CONFIG_MAP.load(inputStream);
                String enableNacos = CONFIG_MAP.getProperty(ENABLE_NACOS_KEY, "false");
                if (Boolean.valueOf(enableNacos)) {
                    SERVER_ADDR = CONFIG_MAP.getProperty(SERVER_ADDR_KEY);
                    NAMESPACE = CONFIG_MAP.getProperty(NAMESPACE_KEY);
                    GROUPS = CONFIG_MAP.getProperty(GROUPS_KEY, DEFAULT_GROUP).split(",");
                    DATA_IDS = CONFIG_MAP.getProperty(DATA_IDS_KEY, DEFAULT_DATA_ID).split(",");
                    TIMEOUT = Integer.valueOf(CONFIG_MAP.getProperty(TIMEOUT_KEY, DEFAULT_TIMEOUT));
                    initNacos();
                } else {
                    logger.debug("Naocs config is not enable");
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("", e);
            }

        }
    }

    private static void initNacos() {
        Properties properties = new Properties();
        properties.put("serverAddr", SERVER_ADDR);
        properties.put("namespace", NAMESPACE);


        String content = null;
        try {
            logger.info("loading config from nacos...");

            ConfigService configService = NacosFactory.createConfigService(properties);
            for (String group : GROUPS) {
                for (String dataId : DATA_IDS) {
                    content = configService.getConfig(dataId, group, TIMEOUT);
                    if (content != null) {
                        NACOS_CONFIG_MAP.load(new StringReader(content));
                        configService.addListener(dataId, group, new PropertiesListener() {
                            @Override
                            public void innerReceive(Properties properties) {
                                logger.info("config file {}:{} refreshed", group, dataId);
                                NACOS_CONFIG_MAP.putAll(properties);
                            }
                        });
                    }
                }
            }
            logger.info("load config from nacos success");

        } catch (NacosException | IOException e) {
            e.printStackTrace();
            logger.error("", e);
        }

    }

    public static String getConfigValue(String key) {
        return NACOS_CONFIG_MAP.getProperty(key);
    }

    public static String getConfigValue(String key, String defaultValue) {
        return NACOS_CONFIG_MAP.getProperty(key, defaultValue);
    }
}
