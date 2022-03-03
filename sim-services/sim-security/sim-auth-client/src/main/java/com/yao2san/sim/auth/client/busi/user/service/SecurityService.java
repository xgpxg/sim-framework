package com.yao2san.sim.auth.client.busi.user.service;

import com.yao2san.sim.auth.client.busi.constant.SystemConstant;
import com.yao2san.sim.auth.client.busi.user.bean.UserAuth;
import com.yao2san.sim.framework.web.response.ResponseData;

import java.util.Map;

public interface SecurityService {


    boolean checkAuthExists(String openid, SystemConstant.LoginType loginType);

    UserAuth qryUserAuth(String openId, SystemConstant.LoginType loginType);
}
