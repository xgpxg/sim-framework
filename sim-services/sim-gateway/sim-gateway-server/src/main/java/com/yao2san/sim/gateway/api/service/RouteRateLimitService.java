package com.yao2san.sim.gateway.api.service;

import com.github.pagehelper.PageInfo;
import com.yao2san.sim.gateway.api.request.RouteRateLimitReq;
import com.yao2san.sim.gateway.api.response.RouteRateLimitRes;

public interface RouteRateLimitService {
    void refresh();

    void refresh(Long routeId);

    RouteRateLimitRes detail(Long routeRateLimitPolicyId);

    PageInfo<RouteRateLimitRes> list(RouteRateLimitReq routeRateLimit);

    void add(RouteRateLimitReq routeRateLimit);

    void delete(Long rateLimitPolicyId);

    void update(RouteRateLimitReq routeRateLimit);
}
