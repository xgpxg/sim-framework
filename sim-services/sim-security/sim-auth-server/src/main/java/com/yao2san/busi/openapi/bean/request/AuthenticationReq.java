package com.yao2san.busi.openapi.bean.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AuthenticationReq {

    /**
     * 租户的openId
     */
    @NotEmpty(message = "openId cannot be empty")
    private String openId;

    /**
     * 租户私钥
     */
    @NotEmpty(message = "secretKey cannot be empty")
    private String secretKey;
}
