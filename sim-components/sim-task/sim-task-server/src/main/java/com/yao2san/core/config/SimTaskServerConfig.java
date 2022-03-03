package com.yao2san.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SimTaskServerConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
