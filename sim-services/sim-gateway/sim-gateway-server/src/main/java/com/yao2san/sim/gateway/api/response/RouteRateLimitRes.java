package com.yao2san.sim.gateway.api.response;

import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RouteRateLimitRes extends Pagination {

    private Long routeId;

    private Long routeRateLimitPolicyId;

    private Long refreshInterval;

    private Long limit;

    private Long quota;

    private boolean breakOnMatch;

    private String matchType;

    private String matcher;

}
