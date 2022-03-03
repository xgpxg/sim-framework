package com.yao2san.sim.gateway.api.service.impl;

import com.github.pagehelper.PageInfo;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import com.yao2san.sim.gateway.api.request.ServiceReq;
import com.yao2san.sim.gateway.api.response.ServiceRes;
import com.yao2san.sim.gateway.api.service.ServiceManagerService;
import org.springframework.stereotype.Service;

@Service
public class ServiceManagerServiceImpl extends BaseServiceImpl implements ServiceManagerService {
    @Override
    public ResponseData list(ServiceReq req) {
        PageInfo<ServiceRes> list = this.qryList("service.list", req);
        return ResponseData.success(list);
    }

    @Override
    public ResponseData add(ServiceReq req) {
        return null;
    }

    @Override
    public ResponseData detail(ServiceReq req) {
        return null;
    }

    @Override
    public ResponseData update(ServiceReq req) {
        return null;
    }

    @Override
    public ResponseData delete(ServiceReq req) {
        return null;
    }

    @Override
    public ResponseData online(ServiceReq req) {
        return null;
    }

    @Override
    public ResponseData offline(ServiceReq req) {
        return null;
    }
}
