package com.yao2san.simtaskclient.executor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.yao2san.sim.framework.utils.CacheUtil;
import com.yao2san.sim.framework.utils.RequestUtil;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.simtaskclient.annotation.SimTask;
import com.yao2san.simtaskclient.bean.SimTaskBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.StringUtils;

import java.util.*;


public class SimTaskExecutorUtil {
    private static final Logger logger = LoggerFactory.getLogger(SimTaskExecutorUtil.class);

    /**
     * 注册定时任务
     *
     * @param simTaskBeans 来自于服务端的任务(非server模式为扫描到的任务)
     */
    public static void registerQuartz(SchedulerFactoryBean schedulerFactoryBean, List<SimTaskBean> simTaskBeans) {
        List<SimTaskBean> cacheSimTaskBeans = CacheUtil.get("SIM_TASK_CACHE");
        if (simTaskBeans != null)
            for (SimTaskBean simTaskBean : simTaskBeans) {
                if (cacheSimTaskBeans != null) {
                    for (SimTaskBean cacheSimTaskBean : cacheSimTaskBeans) {
                        if (simTaskBean.getGroup().equals(cacheSimTaskBean.getGroup()) && !simTaskBean.getGroup().equals("_SYSTEM") && simTaskBean.getTaskName().equals(cacheSimTaskBean.getTaskName())) {
                            if (cacheSimTaskBean.getEnabled()) {
                                if ("normal".equalsIgnoreCase(cacheSimTaskBean.getState()))
                                    SimTaskExecutors.add(schedulerFactoryBean,
                                            cacheSimTaskBean.getClassName(),
                                            cacheSimTaskBean.getTaskName(),
                                            cacheSimTaskBean.getGroup(),
                                            cacheSimTaskBean.getCron()
                                    );
                            }
                        }
                    }
                } else {
                    SimTaskExecutors.add(schedulerFactoryBean,
                            simTaskBean.getClassName(),
                            simTaskBean.getTaskName(),
                            simTaskBean.getGroup(),
                            simTaskBean.getCron()
                    );
                }
            }
    }


  /*  public static boolean cacheSimTask(String serverAddr, String appName) {
        if (StringUtils.isEmpty(serverAddr) || StringUtils.isEmpty(appName)) {
            logger.info("serverAddr or appName is empty, please check application config");
            return false;
        }
        if (!"ok".equals(SimTaskAutoConfiguration.serverState)) {
            logger.info("server state is not ok, cache fail");
            return false;
        }
        String service = "http://" + serverAddr + "/simTaskServer/simTaskList";
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("appName", appName);
        try {
            String result = RequestUtil.post(service, reqParams, String.class);
            ResponseData<List<SimTaskBean>> responseData = JSONObject.parseObject(result, new TypeReference<ResponseData<List<SimTaskBean>>>() {
            });
            if (responseData != null && responseData.getCode() == 1) {
                if (responseData.getData() != null) {
                    List<SimTaskBean> simTaskBeans = responseData.getData();
                    CacheUtil.put("SIM_TASK_CACHE", simTaskBeans);
                    logger.info("sim task list cache success ({} task in total)", simTaskBeans.size());
                    return true;
                } else {
                    logger.warn("get sim task from server result is null");
                }
            }
        } catch (Exception e) {
            logger.error("cache sim task list error, the tasks will be run with default config", e);
        }
        return false;
    }*/

    /**
     * 缓存任务列表
     */
    public static void cacheTask(List<SimTaskBean> simTaskBeans) {
        if (simTaskBeans != null)
            CacheUtil.put("SIM_TASK_CACHE", simTaskBeans);
    }

    public static List<SimTaskBean> getCacheTask() {
        return CacheUtil.get("SIM_TASK_CACHE");
    }

    public static List<SimTaskBean> getTaskFromServer(String serverAddr, String appName) {
        if (StringUtils.isEmpty(serverAddr) || StringUtils.isEmpty(appName)) {
            logger.info("serverAddr or appName is empty, please check application config");
            return null;
        }
        String service = "http://" + serverAddr + "/simTaskServer/simTaskList";
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("appName", appName);
        try {
            String result = RequestUtil.postForObject(service, reqParams, String.class);
            ResponseData<List<SimTaskBean>> responseData = JSONObject.parseObject(result, new TypeReference<ResponseData<List<SimTaskBean>>>() {
            });
            if (responseData != null && responseData.getCode() == 0) {
                if (responseData.getData() != null) {
                    List<SimTaskBean> simTaskBeans = responseData.getData();
                    logger.info("task list get success ({} task in total)", simTaskBeans.size());
                    return simTaskBeans;
                } else {
                    logger.warn("get task from server result is null");
                }
            }
        } catch (Exception e) {
            logger.error("get task list error, the tasks will not be run", e);
        }
        return null;
    }

    /**
     * 服务端计算增减量，获取状态配置（用于启动初始化）
     *
     * @param localTaskBeans 扫描出的
     * @param serverAddr     server地址
     * @param appName        app名称
     * @return
     */
    public static List<SimTaskBean> initTaskFromServer(List<SimTaskBean> localTaskBeans, String serverAddr, String appName) {
        if (StringUtils.isEmpty(serverAddr) || StringUtils.isEmpty(appName)) {
            logger.info("serverAddr or appName is empty, please check application config");
            return null;
        }
        String service = "http://" + serverAddr + "/simTaskServer/register";
        try {
            String result = RequestUtil.postForObject(service, localTaskBeans, String.class);
            ResponseData<List<SimTaskBean>> responseData = JSONObject.parseObject(result, new TypeReference<ResponseData<List<SimTaskBean>>>() {
            });
            if (responseData != null && responseData.getCode() == 0) {
                if (responseData.getData() != null) {
                    List<SimTaskBean> simTaskBeans = responseData.getData();
                    logger.info("task list get success ({} task in total)", simTaskBeans.size());
                    return simTaskBeans;
                } else {
                    logger.warn("get task from server result is null");
                }
            }
        } catch (Exception e) {
            logger.error("get task list error, the tasks will not be run", e);
        }
        return null;
    }

    public static boolean registerServer(String serverAddr, List<SimTaskBean> simTaskBeans) {
        if (serverAddr == null || "".equals(serverAddr)) {
            logger.warn("server address config is null, will not be register to server");
            return false;
        }
        logger.info("start register sim task...");
        String service = "http://" + serverAddr + "/simTaskServer/register";

        try {
            ResponseData result = RequestUtil.postForObject(service, simTaskBeans, ResponseData.class);
            if (result.getCode() == 1) {
                logger.info("task register success ({} task in total)", simTaskBeans.size());
                return true;
            }
        } catch (Exception e) {
            logger.error("task register error, please check server state", e);
        }
        return false;
    }

    public static List<SimTaskBean> buildSimTaskFromClasses(Set<Class<?>> classes) {
        List<SimTaskBean> simTaskBeans = new ArrayList<>();
        for (Class<?> clazz : classes) {
            SimTask simTaskAnnotation = clazz.getAnnotation(SimTask.class);
            String name = simTaskAnnotation.name();
            if ("".equals(name)) {
                name = clazz.getName();
            }
            String cron = simTaskAnnotation.cron();
            SimTaskBean simTaskBean = new SimTaskBean();
            simTaskBean.setTaskName(name);
            simTaskBean.setCron(cron);
            simTaskBean.setClassName(clazz.getName());
            simTaskBean.setGroup(simTaskAnnotation.group());
            simTaskBeans.add(simTaskBean);
            simTaskBean.setEnabled(simTaskAnnotation.enabled());
        }
        return simTaskBeans;
    }
}
