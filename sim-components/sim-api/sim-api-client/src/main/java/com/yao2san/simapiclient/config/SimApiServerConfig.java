package com.yao2san.simapiclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationProperties(prefix = "sim-api.server")
@EnableConfigurationProperties(SimApiServerConfig.class)
@Data
public class SimApiServerConfig {
    private String addr;
}
