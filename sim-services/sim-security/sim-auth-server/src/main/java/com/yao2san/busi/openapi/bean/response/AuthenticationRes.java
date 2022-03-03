package com.yao2san.busi.openapi.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRes {
    /**
     * access token
     */
    private String token;
    /**
     * access token 到期时间
     */
    private String expireDate;
}
