package com.yao2san.sim.gateway.api.controller;

import com.github.pagehelper.PageInfo;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.gateway.api.request.GrayRouteReq;
import com.yao2san.sim.gateway.api.response.GrayRouteRes;
import com.yao2san.sim.gateway.api.response.RouteRes;
import com.yao2san.sim.gateway.api.service.GrayRouteManagerService;
import com.yao2san.sim.gateway.route.gray.AbstractGrayRouteRule;
import com.yao2san.sim.gateway.route.gray.GrayRouteHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * Gray route manage api
 *
 * @author wxg
 */
@RestController
@RequestMapping("grayRoute")
public class GrayRouteManager {
    @SuppressWarnings("all")
    @Autowired
    private AbstractGrayRouteRule grayRouteRule;

    @SuppressWarnings("all")
    @Autowired
    private GrayRouteManagerService grayRouteManagerService;

    @PostMapping("refresh")
    public ResponseData refresh() {
        GrayRouteHelper.refresh();
        return ResponseData.success(null, "success");
    }

    @PatchMapping("/formal/{routeGrayId}")
    public ResponseData formal(@PathVariable Long routeGrayId) {
        grayRouteManagerService.formal(routeGrayId);
        return ResponseData.success(null, "success");
    }


    @PostMapping
    public ResponseData add(@RequestBody @Validated(GrayRouteReq.Add.class) GrayRouteReq grayRouteReq) {
        grayRouteManagerService.add(grayRouteReq);
        return ResponseData.success(null, "success");
    }

    @DeleteMapping("{grayRouteId}")
    public ResponseData delete(@PathVariable @NotNull Long grayRouteId) {
        grayRouteManagerService.delete(grayRouteId);
        return ResponseData.success(null, "success");
    }

    @PatchMapping
    public ResponseData update(@RequestBody @Validated(GrayRouteReq.Update.class) GrayRouteReq grayRouteReq) {
        grayRouteManagerService.update(grayRouteReq);
        return ResponseData.success(null, "success");
    }

    @PatchMapping("/online/{routeGrayId}")
    public ResponseData online(@PathVariable @NotNull Long routeGrayId) {
        grayRouteManagerService.online(routeGrayId);
        return ResponseData.success(null, "success");
    }

    @PatchMapping("/offline/{routeGrayId}")
    public ResponseData offline(@PathVariable @NotNull Long routeGrayId) {
        grayRouteManagerService.offline(routeGrayId);
        return ResponseData.success(null, "success");
    }

    @GetMapping
    public ResponseData list(GrayRouteReq routeReq) {
        PageInfo<RouteRes> list = grayRouteManagerService.list(routeReq);
        return ResponseData.success(list);
    }

    @GetMapping("{routeGrayId}")
    public ResponseData detail(@PathVariable Long routeGrayId) {
        GrayRouteRes detail = grayRouteManagerService.detail(routeGrayId);
        return ResponseData.success(detail);
    }
}
