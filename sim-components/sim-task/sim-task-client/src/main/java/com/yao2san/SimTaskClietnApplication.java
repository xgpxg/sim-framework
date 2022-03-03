package com.yao2san;

import com.yao2san.simtaskclient.annotation.EnabledSimTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnabledSimTask
@RestController
public class SimTaskClietnApplication {
    @RequestMapping("/test")
    public void test() {
    }

    public static void main(String[] args) {
        SpringApplication.run(SimTaskClietnApplication.class, args);
    }

}
