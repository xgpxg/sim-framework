package com.yao2san.sim.gateway.config;

import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class EnvConfiguration{

    @EventListener
    public void envListener(EnvironmentChangeEvent event) {
        System.out.println("conf change: " + event);
    }

}