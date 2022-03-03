package com.yao2san.busi.user.service;

import com.yao2san.busi.constant.SystemConstant;
import com.yao2san.busi.user.bean.UserAuth;
import com.yao2san.sim.framework.web.response.ResponseData;

import java.util.Map;

public interface SecurityService {

    ResponseData login(Map<String, Object> params);

    boolean checkAuthExists(String openid, SystemConstant.LoginType loginType);

    UserAuth addUserAuth(UserAuth userAuth);

    void updateUserAuth(UserAuth userAuth);

    UserAuth qryUserAuth(String openId, SystemConstant.LoginType loginType);
}
