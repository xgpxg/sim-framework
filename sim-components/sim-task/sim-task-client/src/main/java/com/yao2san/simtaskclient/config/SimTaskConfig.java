package com.yao2san.simtaskclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "sim-task")
@EnableConfigurationProperties
@Data
@Configuration
public class SimTaskConfig {
    //@Value("server-addr")
    private String serverAddr;
    //@Value("base-scan-packages")
    private String baseScanPackages;
    //@Value("store-type")
    private String storeType;
}
