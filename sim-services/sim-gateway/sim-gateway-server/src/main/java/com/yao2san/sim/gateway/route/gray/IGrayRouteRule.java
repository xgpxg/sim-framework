package com.yao2san.sim.gateway.route.gray;

public interface IGrayRouteRule {
    GrayRoute matchingGrayRoute();

    boolean isHit(GrayRoute grayRoute);
}
