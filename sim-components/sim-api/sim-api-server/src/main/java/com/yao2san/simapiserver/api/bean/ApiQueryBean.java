package com.yao2san.simapiserver.api.bean;

import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;

@Data
public class ApiQueryBean {
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
    private String reqMethod;
    /**
     * 返回值类型
     */
    private String resType;

    private String resData;
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
    /**
     *
     */
    private Integer openSimulation;
    private String createTime;
    private String updateTime;

    private String status;

    private Pagination pagination;

    private String filter;
}
