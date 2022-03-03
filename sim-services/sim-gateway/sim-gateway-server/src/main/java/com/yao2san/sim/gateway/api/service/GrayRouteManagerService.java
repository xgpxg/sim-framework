package com.yao2san.sim.gateway.api.service;

import com.github.pagehelper.PageInfo;
import com.yao2san.sim.gateway.api.request.GrayRouteReq;
import com.yao2san.sim.gateway.api.response.GrayRouteRes;
import com.yao2san.sim.gateway.api.response.RouteRes;

public interface GrayRouteManagerService {

    GrayRouteRes detail(Long routeGrayId);

    PageInfo<RouteRes> list(GrayRouteReq grayRouteReq);

    void add(GrayRouteReq grayRouteReq);

    void delete(Long routeGrayId);

    void update(GrayRouteReq grayRouteReq);

    void online(Long routeGrayId);

    void offline(Long routeGrayId);

    /**
     * gray to formal
     */
    void formal(Long routeGrayId);

}
