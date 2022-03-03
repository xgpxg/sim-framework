package com.yao2san.dbdif.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import com.yao2san.sim.framework.web.response.ResponseCode;
import com.yao2san.sim.framework.web.response.ResponseData;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 服务全局异常处理
 */
@ControllerAdvice
public class BusiExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(BusiExceptionHandler.class);

    /**
     * 处理业务异常
     *
     * @param e       业务异常
     * @param request 请求内容
     * @return 异常信息
     */
    @ResponseBody
    @ExceptionHandler(BusiException.class)
    public ResponseData handlerBusiException(BusiException e, HttpServletRequest request) {
        e.printStackTrace();
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return ResponseData.error(ResponseCode.UNKNOWN_ERROR.getCode(), ResponseCode.UNKNOWN_ERROR.getDesc());
    }

    /**
     * 处理未知异常
     *
     * @param e       异常
     * @param request 请求
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseData handlerException(Exception e, HttpServletRequest request) throws IOException {
        e.printStackTrace();
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return ResponseData.error(ResponseCode.UNKNOWN_ERROR.getCode(), ResponseCode.UNKNOWN_ERROR.getDesc());
    }
}
