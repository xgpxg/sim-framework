package com.yao2san.busi.user.controller;

import com.yao2san.busi.user.bean.request.TenantServiceReq;
import com.yao2san.busi.user.service.TenantManagerService;
import com.yao2san.sim.framework.web.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author wxg
 * <p>
 * 租户管理接口
 */
@RestController
@RequestMapping("/tenantManager")
public class TenantManagerController {
    @Autowired
    private TenantManagerService tenantManagerService;

    /**
     * 查询租户服务列表
     */
    @GetMapping("/service")
    public ResponseData services(@Validated TenantServiceReq req) {
        return tenantManagerService.services(req);
    }

    /**
     * 服务授权or解除授权
     */
    @PostMapping("/service")
    public ResponseData serviceAuthorization(@RequestBody TenantServiceReq req) {
        return tenantManagerService.serviceAuthorization(req);
    }


    @DeleteMapping("/service")
    public ResponseData deleteServiceAuthorization() {
        return null;
    }
}
