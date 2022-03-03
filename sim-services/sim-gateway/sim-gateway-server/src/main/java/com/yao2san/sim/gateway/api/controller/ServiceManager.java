package com.yao2san.sim.gateway.api.controller;

import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.gateway.api.request.ServiceReq;
import com.yao2san.sim.gateway.api.service.ServiceManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("service-manager")
public class ServiceManager {
    @Autowired
    private ServiceManagerService serviceManagerService;

    @GetMapping
    public ResponseData list(ServiceReq req) {
        return serviceManagerService.list(req);
    }
}
