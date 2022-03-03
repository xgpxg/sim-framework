package com.yao2san.sim.gateway.api.service;

import com.github.pagehelper.PageInfo;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.gateway.api.request.RouteReq;
import com.yao2san.sim.gateway.api.response.RouteRes;
import com.yao2san.sim.gateway.route.core.DynamicRoute;

public interface RouteManagerService {

    RouteRes detail(Long routeId);

    PageInfo<RouteRes> list(RouteReq routeReq);

    void add(RouteReq routeReq);

    void delete(Long routeId);

    void update(RouteReq routeReq);

    void online(Long routeId);

    void offline(Long routeId);
}
