package com.yao2san.sim.gateway.api.controller;

import com.github.pagehelper.PageInfo;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.gateway.api.request.RouteReq;
import com.yao2san.sim.gateway.api.response.RouteRes;
import com.yao2san.sim.gateway.api.service.RouteManagerService;
import com.yao2san.sim.gateway.route.core.AbstractRouteLocator;
import com.yao2san.sim.gateway.route.core.RouteHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * Route manage api
 *
 * @author wxg
 */
@RestController
@RequestMapping("route")
public class RouteManager {
    @SuppressWarnings("all")
    @Autowired
    private AbstractRouteLocator abstractRouteLocator;

    @SuppressWarnings("all")
    @Autowired
    private RouteManagerService routeManagerService;

    @PostMapping("refresh")
    public ResponseData refresh() {
        RouteHelper.refresh();
        return ResponseData.success(null, "success");
    }

    @PatchMapping("/online/{routeId}")
    public ResponseData online(@PathVariable Long routeId) {
        routeManagerService.online(routeId);
        return ResponseData.success(null, "success");
    }

    @PatchMapping("/offline/{routeId}")
    public ResponseData offline(@PathVariable Long routeId) {
        routeManagerService.offline(routeId);
        return ResponseData.success(null, "success");
    }

    @PostMapping
    public ResponseData add(@RequestBody @Validated(RouteReq.Add.class) RouteReq routeReq) {
        routeManagerService.add(routeReq);
        return ResponseData.success(null, "success");
    }

    @DeleteMapping("{routeId}")
    public ResponseData delete(@PathVariable @NotNull Long routeId) {
        routeManagerService.delete(routeId);
        return ResponseData.success(null, "success");
    }

    @PatchMapping
    public ResponseData update(@RequestBody @Validated(RouteReq.Update.class) RouteReq dynamicRoute) {
        routeManagerService.update(dynamicRoute);
        return ResponseData.success(null, "success");
    }

    @GetMapping
    public ResponseData list(RouteReq dynamicRoute) {
        PageInfo<RouteRes> list = routeManagerService.list(dynamicRoute);
        return ResponseData.success(list);
    }

    @GetMapping("{routeId}")
    public ResponseData detail(@PathVariable Long routeId) {
        RouteRes detail = routeManagerService.detail(routeId);
        return ResponseData.success(detail);
    }
}
