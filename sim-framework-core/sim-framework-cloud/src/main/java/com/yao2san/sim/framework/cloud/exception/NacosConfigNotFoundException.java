package com.yao2san.sim.framework.cloud.exception;

/**
 * @author wxg
 * nacos configuration not found exception.
 */
public class NacosConfigNotFoundException extends RuntimeException{
    public NacosConfigNotFoundException(String message) {
        super(message);
    }
}
