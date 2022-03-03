package com.yao2san.busi.constant;

import lombok.Getter;

public class SystemConstant {
    public enum LoginType {
        USERNAME_PASSWORD("1000"),
        PHONE("2000"),
        XCX("3000");
        @Getter
        private String typeName;

        LoginType(String typeName) {
            this.typeName = typeName;
        }


    }

    public static final String SHIRO_AUTH_CACHE_KEY_PREFIX = "shiro:cache:com.yao2san.sim.auth.client.config.UserRealm.authorizationCache:";
}
