package com.yao2san.sim.gateway.api.service;

import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.gateway.api.request.ServiceReq;

public interface ServiceManagerService {
    ResponseData list(ServiceReq req);
    ResponseData add(ServiceReq req);
    ResponseData detail(ServiceReq req);
    ResponseData update(ServiceReq req);
    ResponseData delete(ServiceReq req);
    ResponseData online(ServiceReq req);
    ResponseData offline(ServiceReq req);
}
