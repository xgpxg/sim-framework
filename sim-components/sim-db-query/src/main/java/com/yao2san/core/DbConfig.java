package com.yao2san.core;

import lombok.Data;

@Data
public class DbConfig {
    private String url;
    private String driverClassName;
    private String username;
    private String password;
}
