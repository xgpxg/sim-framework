package com.yao2san.busi.openapi.controller;

import com.yao2san.busi.openapi.bean.request.AuthenticationReq;
import com.yao2san.busi.openapi.bean.response.AuthenticationRes;
import com.yao2san.busi.openapi.service.OauthService;
import com.yao2san.sim.framework.web.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 租户认证相关接口
 */
@RestController
@RequestMapping("oauth")
public class OauthController {

    @Autowired
    private OauthService oauthService;

    /**
     * 信息认证 认证成功返回token和token过期时间
     */
    @PostMapping("token")
    public ResponseData<AuthenticationRes> authenticate(@RequestBody @Validated AuthenticationReq authenticationReq) {
        return oauthService.authenticate(authenticationReq);
    }

}
