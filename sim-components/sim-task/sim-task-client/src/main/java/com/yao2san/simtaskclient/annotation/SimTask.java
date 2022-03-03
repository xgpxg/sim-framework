package com.yao2san.simtaskclient.annotation;

import org.quartz.SchedulerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AutoConfigureAfter(SchedulerFactory.class)
public @interface SimTask {
    String cron();

    String name() default "";

    String group() default "DEFAULT";

    boolean startNow() default false;

    boolean enabled() default true;

}
