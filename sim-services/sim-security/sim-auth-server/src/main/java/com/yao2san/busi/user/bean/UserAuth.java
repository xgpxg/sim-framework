package com.yao2san.busi.user.bean;

import com.yao2san.sim.framework.web.bean.BaseBean;
import lombok.Data;

@Data
public class UserAuth extends BaseBean {
    private Long userAuthId;

    private Long userId;

    private String userType;

    private String loginType;

    private String openid;

    private String accessToken;
}
