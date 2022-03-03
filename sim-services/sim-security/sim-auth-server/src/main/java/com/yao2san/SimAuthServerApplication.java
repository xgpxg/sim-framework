package com.yao2san;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;

@SpringBootApplication
@Slf4j
public class SimAuthServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimAuthServerApplication.class, args);
        log.info("sim-auth-server start success!");
    }
}
