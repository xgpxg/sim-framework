package com.yao2san.simtaskclient.controller;

import com.yao2san.simtaskclient.bean.SimTaskBean;
import com.yao2san.simtaskclient.executor.SimTaskExecutors;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("simTaskClient")
public class SimTaskController {
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @RequestMapping("resume")
    public String resume(@RequestBody SimTaskBean simTaskBean) {
        String state = SimTaskExecutors.getState(schedulerFactoryBean,
                simTaskBean.getTaskName(),
                simTaskBean.getGroup());
        if("NONE".equalsIgnoreCase(state)){
            SimTaskExecutors.add(schedulerFactoryBean,
                    simTaskBean.getClassName(),
                    simTaskBean.getTaskName(),
                    simTaskBean.getGroup(),
                    simTaskBean.getCron());
        }
        return SimTaskExecutors.resume(schedulerFactoryBean,
                simTaskBean.getTaskName(),
                simTaskBean.getGroup()) ? "1" : "0";
    }

    @RequestMapping("suspend")
    public String suspend(@RequestBody SimTaskBean simTaskBean) {
        return SimTaskExecutors.suspend(schedulerFactoryBean,
                simTaskBean.getTaskName(),
                simTaskBean.getGroup()) ? "1" : "0";
    }

    @RequestMapping("stop")
    public String stop(@RequestBody SimTaskBean simTaskBean) {
        return SimTaskExecutors.stop(schedulerFactoryBean,
                simTaskBean.getTaskName(),
                simTaskBean.getGroup()) ? "1" : "0";
    }

    @RequestMapping("start")
    public String start(@RequestBody SimTaskBean simTaskBean) {
        SimTaskExecutors.stop(schedulerFactoryBean,
                simTaskBean.getTaskName(),
                simTaskBean.getGroup());
        return SimTaskExecutors.add(schedulerFactoryBean,
                simTaskBean.getClassName(),
                simTaskBean.getTaskName(),
                simTaskBean.getGroup(),
                simTaskBean.getCron()) ? "1" : "0";
    }

    @RequestMapping("refresh")
    public String refresh(@RequestBody Map<String, SimTaskBean> params) {
        SimTaskBean oldTask = MapUtils.getObject(params, "oldTask");
        SimTaskBean newTask = MapUtils.getObject(params, "newTask");
        SimTaskExecutors.stop(schedulerFactoryBean,
                oldTask.getTaskName(),
                oldTask.getGroup());
        return SimTaskExecutors.add(schedulerFactoryBean,
                newTask.getClassName(),
                newTask.getTaskName(),
                newTask.getGroup(),
                newTask.getCron()) ? "1" : "0";
    }

    /**
     * 立即执行一次
     */
    @RequestMapping("trigger")
    public String trigger(@RequestBody SimTaskBean simTaskBean) {
        return SimTaskExecutors.trigger(schedulerFactoryBean,
                simTaskBean.getTaskName(),
                simTaskBean.getGroup()) ? "1" : "0";
    }

    @RequestMapping("state")
    public String state(@RequestBody SimTaskBean simTaskBean) {
        return SimTaskExecutors.getState(schedulerFactoryBean,
                simTaskBean.getTaskName(),
                simTaskBean.getGroup());
    }
}
