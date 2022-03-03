package com.yao2san.simtaskclient.config;

import com.yao2san.simtaskclient.bean.SimTaskBean;
import com.yao2san.simtaskclient.executor.SimTaskExecutorUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import com.yao2san.sim.framework.utils.CacheUtil;

import java.util.List;

@Component
public class TimingCacheTask implements Job {
    private final static Logger logger = LoggerFactory.getLogger(TimingCacheTask.class);
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        final String serverAddr = jobDataMap.getString("serverAddr");
        final String appName = jobDataMap.getString("appName");
        //缓存
        List<SimTaskBean> taskFromServer = SimTaskExecutorUtil.getTaskFromServer(serverAddr, appName);
        SimTaskExecutorUtil.cacheTask(taskFromServer);
        //添加/修改
        List<SimTaskBean> simTaskBeans = CacheUtil.get("SIM_TASK_CACHE");
        SimTaskExecutorUtil.registerQuartz(schedulerFactoryBean, simTaskBeans);
    }
}
