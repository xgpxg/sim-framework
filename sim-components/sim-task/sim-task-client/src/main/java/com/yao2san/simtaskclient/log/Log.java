package com.yao2san.simtaskclient.log;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;
import com.yao2san.sim.framework.utils.RequestUtil;

import javax.annotation.PostConstruct;

@Component
public class Log {
    private static final Logger logger = LoggerFactory.getLogger(Log.class);

    @Value("${sim-task.server-addr}")
    private String _serverAddr;

    private static String serverAddr;

    @PostConstruct
    public void setServerAddr() {
        serverAddr = "http://" + this._serverAddr + "/simTaskServer/log";
    }

    public static void debug(JobExecutionContext jobExecutionContext, String log) {
        logger.debug(log);
        LogMsg logMsg = new LogMsg(
                jobExecutionContext.getFireInstanceId(),
                jobExecutionContext.getJobDetail().getJobClass().getName(),
                LogLevel.DEBUG,
                log);
        RequestUtil.postForObject(serverAddr, logMsg, String.class);
    }

    public static void info(JobExecutionContext jobExecutionContext, String log) {
        logger.info(log);
        LogMsg logMsg = new LogMsg(
                jobExecutionContext.getFireInstanceId(),
                jobExecutionContext.getJobDetail().getJobClass().getName(),
                LogLevel.INFO,
                log);
        RequestUtil.postForObject(serverAddr, logMsg, String.class);
    }

    public static void warn(JobExecutionContext jobExecutionContext, String log) {
        logger.warn(log);
        LogMsg logMsg = new LogMsg(
                jobExecutionContext.getFireInstanceId(),
                jobExecutionContext.getJobDetail().getJobClass().getName(),
                LogLevel.WARN,
                log);
        RequestUtil.postForObject(serverAddr, logMsg, String.class);
    }

    public static void error(JobExecutionContext jobExecutionContext, String log) {
        logger.error(log);
        LogMsg logMsg = new LogMsg(
                jobExecutionContext.getFireInstanceId(),
                jobExecutionContext.getJobDetail().getJobClass().getName(),
                LogLevel.ERROR,
                log);
        RequestUtil.postForObject(serverAddr, logMsg, String.class);
    }
}
