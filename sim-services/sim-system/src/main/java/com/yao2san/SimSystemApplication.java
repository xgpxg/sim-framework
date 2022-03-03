package com.yao2san;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class SimSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimSystemApplication.class, args);
        log.info("sim-system-server start success!");
    }
}
