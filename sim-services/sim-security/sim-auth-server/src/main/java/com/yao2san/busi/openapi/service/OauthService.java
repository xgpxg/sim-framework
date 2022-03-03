package com.yao2san.busi.openapi.service;

import com.yao2san.busi.openapi.bean.request.AuthenticationReq;
import com.yao2san.busi.openapi.bean.response.AuthenticationRes;
import com.yao2san.sim.framework.web.response.ResponseData;

public interface OauthService {
    /**
     * 租户信息认证
     */
    ResponseData<AuthenticationRes> authenticate(AuthenticationReq authenticationReq);
}
