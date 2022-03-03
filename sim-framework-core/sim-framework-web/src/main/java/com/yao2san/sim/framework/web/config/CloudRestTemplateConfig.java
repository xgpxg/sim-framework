package com.yao2san.sim.framework.web.config;

import org.springframework.boot.autoconfigure.cloud.CloudServiceConnectorsAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

/**
 * @author wxg
 */
@Configuration
@ConditionalOnClass(CloudServiceConnectorsAutoConfiguration.class)
public class CloudRestTemplateConfig {
    @Bean("cloudRestTemplate")
    @LoadBalanced
    public RestTemplate cloudRestTemplate() {
        return new RestTemplate();
    }

    @Bean("restTemplate")
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
