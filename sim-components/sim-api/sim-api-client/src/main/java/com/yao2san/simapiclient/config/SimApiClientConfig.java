package com.yao2san.simapiclient.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({SimApiClientConfig.class})
@ConfigurationProperties(prefix = "sim-api.client")
@Data
public class SimApiClientConfig {
    private String host;
}
