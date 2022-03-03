package com.yao2san.busi.user.service;

import com.yao2san.busi.user.bean.request.TenantServiceReq;
import com.yao2san.sim.framework.web.response.ResponseData;

public interface TenantManagerService {
    ResponseData services(TenantServiceReq req);
    ResponseData serviceAuthorization(TenantServiceReq req);

}
