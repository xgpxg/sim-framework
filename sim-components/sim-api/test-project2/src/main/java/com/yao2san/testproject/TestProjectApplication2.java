package com.yao2san.testproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class TestProjectApplication2 {

    public static void main(String[] args) {
        SpringApplication.run(TestProjectApplication2.class, args);
    }

}
