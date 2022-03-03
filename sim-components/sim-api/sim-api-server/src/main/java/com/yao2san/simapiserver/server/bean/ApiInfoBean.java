package com.yao2san.simapiserver.server.bean;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class ApiInfoBean {
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 应用ulr
     */
    private String appAddr;
    /**
     * api名称
     */
    private String apiName;
    /**
     * api
     */
    private String apiUrl;
    /**
     * 请求类型，get/post/patch/delete
     */
    private String requestType;
    /**
     * 返回值类型
     */
    private String returnType;
    /**
     * 方法
     */
    private String method;
    /**
     * api描述
     */
    private String apiDesc;

    /**
     * api类型:1000 已存在的,2000 手动添加的
     */
    private String apiType;

    private String status;
    /**
     *
     */
    private Integer openSimulation;
    private String createTime;
    private String updateTime;
}
