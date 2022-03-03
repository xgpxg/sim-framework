package com.yao2san.simapiclient.register.bean;

import lombok.Data;

import java.util.Set;

@Data
public class SimApiSimpleInfo {
    private String apiId;
    private String apiUrl;
    private String apiName;
    private String appName;
    private String appAddr;
    private String reqMethod;
    private int openSimulation;
    private String status;
    private String apiType;


}
