package com.yao2san.sim.framework.web.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.yao2san.sim.framework.web.response.ResponseCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BusiException extends RuntimeException {
    private int code;
    private String message;

    public BusiException() {
        super();
    }

    public BusiException(String message) {
        this.code = ResponseCode.ERROR.getCode();
        this.message = message;
    }

    public BusiException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
