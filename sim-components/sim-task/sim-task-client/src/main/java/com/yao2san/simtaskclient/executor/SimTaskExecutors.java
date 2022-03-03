package com.yao2san.simtaskclient.executor;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Map;

@SuppressWarnings("unchecked")
public class SimTaskExecutors {
    private static final Logger logger = LoggerFactory.getLogger(SimTaskExecutors.class);

    public static boolean add(SchedulerFactoryBean factory, String className, String taskName, String group, String cron) {
        return add(factory, className, taskName, group, cron, null);
    }

    /**
     * 添加任务
     */
    public static boolean add(SchedulerFactoryBean factory, String className, String taskName, String group, String cron, Map<String, Object> params) {
        Scheduler scheduler = factory.getScheduler();
        try {
            boolean exists = scheduler.checkExists(new JobKey(taskName, group));
            if (exists) {
                if ("normal".equalsIgnoreCase(getState(factory, taskName, group))) {
                    logger.warn("task {}.{} is running, will be stop and update", group, taskName);
                    stop(factory, taskName, group);
                }
                //logger.info("task {}.{} exists, cant't repeat add", group, taskName);
                //return;
            }

            Class<? extends Job> clazz = (Class<? extends Job>) Class.forName(className);
            JobBuilder jobBuilder = JobBuilder.newJob(clazz)
                    .withIdentity(taskName, group);
            if (params != null) {
                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.putAll(params);
                jobBuilder.setJobData(jobDataMap);
            }
            JobDetail jobDetail = jobBuilder.build();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .withIdentity(taskName, group)
                    .build();
            scheduler.scheduleJob(jobDetail, trigger);
            return true;
        } catch (ClassNotFoundException | SchedulerException e) {
            logger.error("task add fail", e);
        }
        return false;
    }

    /**
     * 暂停任务
     */
    public static boolean suspend(SchedulerFactoryBean factory, String taskName, String group) {
        Scheduler scheduler = factory.getScheduler();
        try {
            scheduler.pauseTrigger(new TriggerKey(taskName, group));
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 停止任务
     *
     * @param factory
     * @param taskName
     * @param group
     */
    public static boolean stop(SchedulerFactoryBean factory, String taskName, String group) {
        Scheduler scheduler = factory.getScheduler();
        try {
            scheduler.pauseTrigger(new TriggerKey(taskName, group));
            scheduler.unscheduleJob(new TriggerKey(taskName, group));
            scheduler.deleteJob(new JobKey(taskName, group));
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 恢复任务
     */
    public static boolean resume(SchedulerFactoryBean factory, String taskName, String group) {
        Scheduler scheduler = factory.getScheduler();
        try {
            scheduler.resumeJob(new JobKey(taskName, group));
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 立即执行
     */
    public static boolean trigger(SchedulerFactoryBean factory, String taskName, String group) {
        boolean success = false;
        Scheduler scheduler = factory.getScheduler();
        try {
            scheduler.triggerJob(new JobKey(taskName, group));
            success = true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 立即执行
     */
    public static boolean trigger(SchedulerFactoryBean factory, String taskName, String group, Map<String, Object> params) {
        boolean success = false;
        Scheduler scheduler = factory.getScheduler();
        try {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.putAll(params);
            scheduler.triggerJob(new JobKey(taskName, group), jobDataMap);
            success = true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return success;
    }

    public static String getState(SchedulerFactoryBean factory, String taskName, String group) {
        Scheduler scheduler = factory.getScheduler();
        String name;
        try {
            Trigger.TriggerState triggerState = scheduler.getTriggerState(new TriggerKey(taskName, group));
            name = triggerState.name();
        } catch (SchedulerException e) {
            e.printStackTrace();
            name = "get state exception";
        }
        return name;
    }

    public static void sendLog(String type, String log) {

    }
}
