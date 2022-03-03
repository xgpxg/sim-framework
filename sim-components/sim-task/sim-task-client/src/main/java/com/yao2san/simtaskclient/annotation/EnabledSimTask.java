package com.yao2san.simtaskclient.annotation;

import com.yao2san.simtaskclient.config.JobFactory;
import com.yao2san.simtaskclient.config.SimTaskAutoConfiguration;
import com.yao2san.simtaskclient.log.Log;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableScheduling
@Import({JobFactory.class,SimTaskAutoConfiguration.class, Log.class})
public @interface EnabledSimTask {
}
