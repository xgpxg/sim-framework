package com.yao2san.sim.gateway.rate.core;

import com.yao2san.sim.gateway.route.core.DynamicRoute;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class RouteRateLimit extends DynamicRoute {

    private Long routeId;

    private Long routeRateLimitPolicyId;

    private Long refreshInterval;

    private Long limit;

    private Long quota;

    private boolean breakOnMatch;

    private String matchType;

    private String matcher;

}
