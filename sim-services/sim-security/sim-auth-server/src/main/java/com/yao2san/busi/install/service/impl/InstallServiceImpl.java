package com.yao2san.busi.install.service.impl;

import com.yao2san.busi.install.service.InstallService;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class InstallServiceImpl extends BaseServiceImpl implements InstallService {
    @Override
    public void install(Long serviceId, String version) {
        //获取服务信息

        //下载服务

        //启动服务

        //添加菜单

        //赋予权限

        //写入服务实例
    }

    private void getServiceInfo() {
    }

    private void download() {

    }

    private void addMenu() {

    }

    /**
     * 执行启动脚本
     *
     * @param command 启动命令
     */
    private void startService(String command) {

    }
}
