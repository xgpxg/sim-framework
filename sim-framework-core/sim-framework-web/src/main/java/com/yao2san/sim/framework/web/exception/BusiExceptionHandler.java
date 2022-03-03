package com.yao2san.sim.framework.web.exception;

import com.alibaba.fastjson.JSONObject;
import com.yao2san.sim.framework.web.response.ResponseCode;
import com.yao2san.sim.framework.web.response.ResponseData;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务全局异常处理
 * 抛出异常时记录请求参数，服务名，traceId，异常原因，请求IP，sessionId，userId等等信息
 */
@ControllerAdvice
@SuppressWarnings("all")
public class BusiExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(BusiExceptionHandler.class);

    //@Autowired(required = false)
    //protected Tracer tracer;

    @Value("${spring.application.name}")
    public String SERVICE_NAME;

/*
    @Override
    public String getServiceName() {
        return this.SERVICE_NAME;
    }
    abstract public String getServiceName();*/

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
        logger.error("", e);
        //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        String params = JSONObject.toJSONString(request.getParameterMap());
        //返回错误信息(不包含详细信息)
        ResponseData responseData = ResponseData.error(ResponseCode.BUSINESS_EXCEPTION.getCode(), ResponseCode.BUSINESS_EXCEPTION.getDesc() + ":" + e.getMessage());
        //请求方法
        String method = request.getMethod();
        //服务名称
        String serviceName = this.SERVICE_NAME;
        //traceId
        //String traceId = tracer.currentSpan().context().traceIdString();

        //logger.error("SERVICE:{};ERROR:{};TRACER-ID:{};METHOD:{};PARAMS:{}",serviceName,e.getMessage(),traceId,method,params);
        logger.error("SERVICE:{};ERROR:{};METHOD:{}", serviceName, e.getMessage(), method);
        return responseData;
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
        logger.error("", e);
        //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        if (e instanceof BindException) {
            return handleBindException((BindException) e);
        }
        if (e instanceof MethodArgumentNotValidException) {
            return handleMethodArgumentNotValidException((MethodArgumentNotValidException) e);
        }
        //请求方法
        String method = request.getMethod();
        //服务名称
        String serviceName = this.SERVICE_NAME;
        //traceId
        //String traceId = tracer.currentSpan().context().traceIdString();

        //logger.error("SERVICE:{};ERROR:{};TRACER-ID:{};METHOD:{};PARAMS:{}",serviceName,e.getMessage(),traceId,method,params);
        logger.error("SERVICE:{};ERROR:{};METHOD:{}", serviceName, e.getMessage(), method);
        //返回错误信息(不包含详细信息)
        return ResponseData.error(ResponseCode.SERVER_ERROR, e.getMessage());
    }

    /**
     * Handle validation exception
     */
    private ResponseData handleBindException(BindException e) {
        logger.error("Rest service exception", e);
        List<FieldError> fieldErrorList = e.getBindingResult().getFieldErrors();
        String message = getMessage(fieldErrorList);
        return ResponseData.error(ResponseCode.ILLEGAL_ARGUMENT, message);
    }

    /**
     * Handle validation exception
     */
    private ResponseData handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("Rest service exception", e);
        List<FieldError> fieldErrorList = e.getBindingResult().getFieldErrors();
        String message = getMessage(fieldErrorList);
        return ResponseData.error(ResponseCode.ILLEGAL_ARGUMENT, message);
    }

    private String getMessage(List<FieldError> fieldErrorList) {
        List<String> messages = new ArrayList<>(8);
        if (!CollectionUtils.isEmpty(fieldErrorList)) {
            for (FieldError fieldError : fieldErrorList) {
                if (fieldError != null && fieldError.getDefaultMessage() != null) {
                    messages.add(fieldError.getDefaultMessage());
                }
            }
        }
        return String.join(",", messages);
    }

}
