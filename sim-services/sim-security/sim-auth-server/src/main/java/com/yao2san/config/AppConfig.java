package com.yao2san.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 程序配置
 */
@Configuration
@ConfigurationProperties(prefix = "app.config")
@Data
public class AppConfig {

    private String loginUrl;

    private String notFoundUrl;

    private String localTempFileDir;
    /**
     * 微信小程序相关配置
     */
    private Map<String, String> wx;
}
