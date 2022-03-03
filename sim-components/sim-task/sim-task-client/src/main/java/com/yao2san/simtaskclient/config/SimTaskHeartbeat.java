/*
package com.yao2san.simtaskclient.config;

import com.yao2san.common.utils.RequestUtil;
import com.yao2san.simtaskclient.core.util.SimTaskUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SimTaskHeartbeat implements Job {
    private final static Logger logger = LoggerFactory.getLogger(SimTaskHeartbeat.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        final String serverAddr = jobDataMap.getString("serverAddr");
        final String appName = jobDataMap.getString("appName");
        final int appPort = jobDataMap.getIntValue("appPort");
        if (serverAddr == null || "".equals(serverAddr)) {
            logger.error("server address config is null, will not be register to server and no hearbeat");
        }
        String service = "http://" + serverAddr + "//simTaskServer/state";

        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("appName", appName);
        reqParams.put("appAddr", SimTaskUtil.getLocalHost() + ":" + appPort);

        try {
            String result = RequestUtil.post(service, reqParams, String.class);
            if ("ok".equalsIgnoreCase(result)) {
                SimTaskAutoConfiguration.serverState = "ok";
                logger.info("[hearbeat]server state is ok");
            } else {
                SimTaskAutoConfiguration.serverState = result;
                logger.warn("[hearbeat]server state is not ok: {}", result);
            }
        } catch (Exception e) {
            logger.error("[hearbeat]server state is not ok", e);
        }
    }
}
*/
