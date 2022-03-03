package com.yao2san.simtaskclient.test;

import com.yao2san.simtaskclient.annotation.SimTask;
import com.yao2san.simtaskclient.log.Log;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@SimTask(name = "测试任务", cron = "0/5 * * * * ?")
public class TaskTest2 implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            Log.debug(jobExecutionContext, "开始执行");
            Log.info(jobExecutionContext, "执行中...");
            Log.warn(jobExecutionContext, "警告日志");
            Log.info(jobExecutionContext, "任务结束");
            throw new RuntimeException("测试抛出异常");
        } catch (Exception e) {
            e.printStackTrace();
            Log.error(jobExecutionContext, e.getMessage());
        }

    }
}
