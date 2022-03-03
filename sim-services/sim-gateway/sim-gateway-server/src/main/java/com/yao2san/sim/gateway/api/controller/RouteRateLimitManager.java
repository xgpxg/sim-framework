package com.yao2san.sim.gateway.api.controller;

import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.gateway.api.request.RouteRateLimitReq;
import com.yao2san.sim.gateway.api.service.RouteRateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Rate manage api
 *
 * @author wxg
 */
@RestController
@RequestMapping("rateLimit")
public class RouteRateLimitManager {

    @SuppressWarnings("all")
    @Autowired
    private RouteRateLimitService ratelimitServiceRoute;

    @PostMapping("refresh")
    public ResponseData refresh() {
        ratelimitServiceRoute.refresh();
        return ResponseData.success(null, "success");
    }

    @PostMapping("refresh/{routeId}")
    public ResponseData refreshByRouteId(@PathVariable Long routeId) {
        ratelimitServiceRoute.refresh(routeId);
        return ResponseData.success(null, "success");
    }

    @PostMapping
    public ResponseData add(@RequestBody @Validated(RouteRateLimitReq.Add.class) RouteRateLimitReq routeRateLimit) {
        ratelimitServiceRoute.add(routeRateLimit);
        ratelimitServiceRoute.refresh(routeRateLimit.getRouteId());
        return ResponseData.success();
    }

    @DeleteMapping
    public ResponseData delete(Long routeRateLimitPolicyId) {
        ratelimitServiceRoute.delete(routeRateLimitPolicyId);
        return ResponseData.success("success");
    }

    @PatchMapping
    public ResponseData update(@RequestBody RouteRateLimitReq routeRateLimit) {
        ratelimitServiceRoute.update(routeRateLimit);
        return ResponseData.success("success");
    }
}
