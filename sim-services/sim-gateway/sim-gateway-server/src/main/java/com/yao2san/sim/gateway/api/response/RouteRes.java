package com.yao2san.sim.gateway.api.response;

import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class RouteRes extends Pagination {
    private Long routeId;

    private String routeName;

    private Integer groupId;

    private String version;

    private String status;

    private String type;

    private String prefix;

    private String id;

    private String path;

    private String serviceId;

    private String url;

    private boolean stripPrefix = true;

    private Boolean retryable;

    private Set<String> sensitiveHeaders;

    private boolean customSensitiveHeaders = false;

}
