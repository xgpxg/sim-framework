package com.yao2san.sim.framework.utils.ftpUtil;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ftp")
@Data
public class FtpConfig {
    private String host;
    private String username;
    private String password;
    private int port;
    private Type type;
    private String rootPath;
    private String privateKey;
    enum Type {
        ftp, sftp
    }
}
