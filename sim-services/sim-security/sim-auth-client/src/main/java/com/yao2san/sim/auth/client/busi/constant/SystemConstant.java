package com.yao2san.sim.auth.client.busi.constant;

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
}
