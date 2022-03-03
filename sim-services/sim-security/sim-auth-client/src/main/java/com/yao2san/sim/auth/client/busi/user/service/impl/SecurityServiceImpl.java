package com.yao2san.sim.auth.client.busi.user.service.impl;

import com.yao2san.sim.auth.client.busi.constant.SystemConstant;
import com.yao2san.sim.auth.client.busi.user.bean.UserAuth;
import com.yao2san.sim.auth.client.busi.user.service.SecurityService;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SecurityServiceImpl extends BaseServiceImpl implements SecurityService {

    @Override
    public boolean checkAuthExists(String openid, SystemConstant.LoginType loginType) {
        UserAuth userAuth = new UserAuth();
        userAuth.setOpenid(openid);
        userAuth.setLoginType(loginType.getTypeName());
        Integer count = this.sqlSession.selectOne("user.checkAuthExists", userAuth);
        return count > 0;
    }

    @Override
    public UserAuth qryUserAuth(String openId, SystemConstant.LoginType loginType) {
        UserAuth userAuth = new UserAuth();
        userAuth.setOpenid(openId);
        userAuth.setLoginType(loginType.getTypeName());
        return this.sqlSession.selectOne("user.qryUserAuth", userAuth);
    }
}
