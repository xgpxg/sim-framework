package com.yao2san.busi.install.bean;

import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 服务实例
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceInstance extends Pagination {
    private Long serviceInstanceId;

    private Long serviceId;

    private String serviceName;

    private String version;

    private String storageUrl;

    private String serviceType;

    private String configJson;


}
