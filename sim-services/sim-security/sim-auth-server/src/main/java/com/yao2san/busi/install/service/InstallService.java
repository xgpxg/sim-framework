package com.yao2san.busi.install.service;

/**
 * 服务安装
 */
public interface InstallService {
    /**
     * 服务安装
     *
     * @param serviceId 服务ID
     * @param version   版本号
     */
    void install(Long serviceId, String version);
}
