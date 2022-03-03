package com.yao2san.simtaskclient.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.logging.LogLevel;

@Data
@AllArgsConstructor
public class LogMsg {
    private String instanceId;
    private String jobClassName;
    private LogLevel level;
    private String log;

}
