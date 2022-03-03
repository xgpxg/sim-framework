package com.yao2san.sim.framework.web.response;

public enum ResponseCode {
    //成功
    SUCCESS(0, "SUCCESS"),
    //失败
    ERROR(1, "ERROR"),
    //参数异常
    ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT"),
    //访问受限
    ACCESS_RESTRICTED(3, "ACCESS_RESTRICTED"),
    //服务器错误
    SERVER_ERROR(4, "SERVER_ERROR"),
    //需要登陆
    NEED_LOGIN(5, "NEED_LOGIN"),
    //业务异常
    BUSINESS_EXCEPTION(6, "BUSINESS_EXCEPTION"),
    //未知错误
    UNKNOWN_ERROR(999, "UNKNOWN_ERROR");
    private final int code;
    private final String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
