package com.yao2san.server.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.logging.LogLevel;

@Data
@AllArgsConstructor
@ToString
public class LogMsg {
    private String instanceId;
    private String jobClassName;
    private LogLevel level;
    private String log;

    private int simTaskId;
}
