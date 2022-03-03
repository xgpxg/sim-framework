package com.yao2san.simtaskclient.bean;

import lombok.Data;

import java.util.Map;

@Data
public class SimTaskExecuteBean {
    private String className;
    private String methodName;

    private Map<String, Object> taskParams;
}
