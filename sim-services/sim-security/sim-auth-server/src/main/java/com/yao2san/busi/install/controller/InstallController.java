package com.yao2san.busi.install.controller;

import com.yao2san.busi.install.service.InstallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO 服务安装
 */
@RestController
@RequestMapping("service")
public class InstallController {

    @Autowired
    private InstallService installService;
    /**
     * 服务安装
     *
     * @param serviceId 服务ID
     * @param version   版本号
     */
    @GetMapping("install")
    public void installService(Long serviceId, String version) {
        installService.install(serviceId,version);
    }


}
