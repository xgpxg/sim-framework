package com.yao2san.sim.gateway.route.gray;

import com.yao2san.sim.gateway.config.GatewayProperties;

import java.util.List;
import java.util.Map;

public interface IGrayRouteLocator {
    Map<String, GatewayProperties.GrayRoute> locateGrayRoutes();
    List<GrayRoute> getAll();
}
