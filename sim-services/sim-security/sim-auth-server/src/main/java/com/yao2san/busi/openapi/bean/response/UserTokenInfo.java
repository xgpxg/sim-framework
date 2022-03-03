package com.yao2san.busi.openapi.bean.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * token缓存信息
 */
@Data
public class UserTokenInfo {

    /**
     * 用户标识
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * open id
     */
    private String openId;

    /**
     * 用户属性
     */
    private Map<String, Object> attr;

    /**
     * 已授权服务
     */
    private List<Service> services = new ArrayList<>();

    @Data
    public static class Service {
        /**
         * 服务标识
         */
        private Long serviceId;
        /**
         * 服务名称
         */
        private String name;
        /**
         * 服务地址
         */
        private String url;
    }

}
