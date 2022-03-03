package com.yao2san.sim.framework.web.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseData<T> implements Serializable {
    private int code;
    private String codeDesc;
    private T data;
    private String msg;
    private long timestamp = System.currentTimeMillis();
    private ResponseData(){}
    private  ResponseData(int status,String msg,T data) {
        this.code = status;
        this.msg = msg;
        this.data = data;
    }
    private  ResponseData(int status,String msg) {
        this.code = status;
        this.msg = msg;
    }
    private  ResponseData(ResponseCode status,String msg) {
        this.code = status.getCode();
        this.codeDesc = status.getDesc();
        this.msg = msg;
    }
    public static <T>ResponseData<T> success(){
        return new ResponseData<>(ResponseCode.SUCCESS.getCode(),"",null);
    }
    public static <T>ResponseData<T> success(T data){
        return new ResponseData<>(ResponseCode.SUCCESS.getCode(), "", data);
    }
    public static <T>ResponseData<T> success(T data,String msg){
        return new ResponseData<>(ResponseCode.SUCCESS.getCode(),msg,data);
    }
    public static <T>ResponseData<T> error(){
        return new ResponseData<>(ResponseCode.ERROR,"");
    }
    public static <T>ResponseData<T> error(String msg){
        return new ResponseData<>(ResponseCode.ERROR,msg);
    }
    public static <T>ResponseData<T> businessError(String msg){
        return new ResponseData<>(ResponseCode.BUSINESS_EXCEPTION,msg);
    }
    public static <T>ResponseData<T> error(int status,String msg){
        return new ResponseData<>(status,msg);
    }
    public static <T>ResponseData<T> error(ResponseCode status){
        return new ResponseData<>(status,"");
    }
    public static <T>ResponseData<T> error(ResponseCode status,String msg){
        return new ResponseData<>(status,msg);
    }
    public static <T>ResponseData<T> accessRestricted(int status,String msg){
        return new ResponseData<>(ResponseCode.ACCESS_RESTRICTED.getCode(),msg);
    }
}
