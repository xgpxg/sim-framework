package com.yao2san.simtaskclient.test;

import com.yao2san.simtaskclient.annotation.SimTask;
import com.yao2san.simtaskclient.log.Log;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@SimTask(name = "测试2", cron = "0/1 * * * * ? ")
public class MainTest2 implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        Log.info(jobExecutionContext, "开始执行");
        Log.info(jobExecutionContext, "执行完毕");
    }
}
