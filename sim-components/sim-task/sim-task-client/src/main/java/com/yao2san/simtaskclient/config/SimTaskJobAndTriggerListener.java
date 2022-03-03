package com.yao2san.simtaskclient.config;

import com.yao2san.simtaskclient.log.Log;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimTaskJobAndTriggerListener implements TriggerListener, JobListener {
    private final static Logger logger = LoggerFactory.getLogger(SimTaskJobAndTriggerListener.class);

    @Override
    public String getName() {
        return "sim-task-job-trigger-listener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
        String jobName = jobExecutionContext.getJobDetail().getKey().getName();
        //Log.info("执行job" + jobName);
        //logger.info(jobName + ":执行中");
        //logger.info(jobExecutionContext.getFireInstanceId());
        //Log.info(jobExecutionContext.getFireInstanceId());

    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
        //logger.info(jobExecutionContext.getJobDetail().getKey().getName() + ":执行完成");
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext jobExecutionContext) {
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext jobExecutionContext) {
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {

    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext jobExecutionContext, Trigger.CompletedExecutionInstruction completedExecutionInstruction) {

    }
}
