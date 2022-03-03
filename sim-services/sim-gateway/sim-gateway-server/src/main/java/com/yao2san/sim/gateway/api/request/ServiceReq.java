package com.yao2san.sim.gateway.api.request;

import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceReq extends Pagination {
    private Long serviceId;

    private String name;

    private String url;

    private String type;

    private Long routeId;
    
}
