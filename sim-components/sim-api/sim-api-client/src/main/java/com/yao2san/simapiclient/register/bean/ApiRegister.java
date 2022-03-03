package com.yao2san.simapiclient.register.bean;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Set;

@Data
public class ApiRegister {
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 应用地址
     */
    private String appAddr;
    /**
     * api名称
     */
    private String apiName;
    /**
     * api的url
     */
    private Set<String> urls;
    /**
     * 请求类型，get/post/patch/delete
     */
    private Set<RequestMethod> requestMethods;
    /**
     * 返回值类型
     */
    private String returnType;
    /**
     * api描述
     */
    private String apiDesc;
    /**
     * API方法元数据
     */
    private ApiMethod method;

    private String status;

    private String createTime;

    private String updateTime;
}
