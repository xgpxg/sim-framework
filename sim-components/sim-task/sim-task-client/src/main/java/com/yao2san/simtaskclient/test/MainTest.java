package com.yao2san.simtaskclient.test;

import com.yao2san.simtaskclient.annotation.EnabledSimTask;
import com.yao2san.simtaskclient.annotation.SimTask;
import com.yao2san.simtaskclient.config.SimTaskConfig;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.Date;
//@SimTask(name = "测试", cron = "0/1 * * * * ? ")
@Data
public class MainTest implements Job {
    @Autowired
    private  SimTaskConfig simTaskConfig;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {


    }
}
