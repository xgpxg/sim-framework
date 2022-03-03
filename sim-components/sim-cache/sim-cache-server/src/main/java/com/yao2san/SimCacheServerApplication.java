package com.yao2san;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@Slf4j
public class SimCacheServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimCacheServerApplication.class, args);
        log.info("sim-cache-server启动成功");
    }
}
