package com.yao2san.simapiclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({SimApiEnabled.class})
@ConfigurationProperties(prefix = "sim-api")
@Data
public class SimApiEnabled {
    private String enabled;
}
