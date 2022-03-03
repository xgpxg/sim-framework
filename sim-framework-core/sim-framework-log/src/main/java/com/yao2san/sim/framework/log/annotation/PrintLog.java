package com.yao2san.sim.framework.log.annotation;

import com.yao2san.sim.framework.log.core.LogAppender;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PrintLog {
    @AliasFor("value")
    String name();

    String prefix() default "";

    boolean useTimer() default false;

    Class<LogAppender> appender() default LogAppender.class;
}
