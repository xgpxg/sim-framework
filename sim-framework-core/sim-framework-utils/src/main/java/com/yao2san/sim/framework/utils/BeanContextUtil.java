package com.yao2san.sim.framework.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanContextUtil implements ApplicationContextAware {


    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanContextUtil.applicationContext = applicationContext;
    }

    public static void setContext(ApplicationContext applicationContext) throws BeansException {
        BeanContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clz) throws BeansException {
        return applicationContext.getBean(clz);
    }

    public static <T> T getBean(String name, Class<T> clz) throws BeansException {
        return (T) applicationContext.getBean(name, clz);
    }
}
