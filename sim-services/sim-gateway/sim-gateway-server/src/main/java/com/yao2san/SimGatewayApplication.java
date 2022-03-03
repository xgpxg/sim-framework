package com.yao2san;

import com.yao2san.sim.gateway.rate.core.RateLimitHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableZuulProxy
@SpringBootApplication
@Slf4j
@EnableEurekaClient
@EnableScheduling
public class SimGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimGatewayApplication.class, args);

        log.info("sim-gateway-server start success!");

        RateLimitHelper.refresh();

    }
}
