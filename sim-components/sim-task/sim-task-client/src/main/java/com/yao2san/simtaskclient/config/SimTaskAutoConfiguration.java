package com.yao2san.simtaskclient.config;


import com.yao2san.simtaskclient.annotation.SimTask;
import com.yao2san.simtaskclient.bean.SimTaskBean;
import com.yao2san.simtaskclient.core.util.SimTaskUtil;
import com.yao2san.simtaskclient.executor.SimTaskExecutorUtil;
import com.yao2san.simtaskclient.executor.SimTaskExecutors;
import org.quartz.SchedulerException;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//import com.yao2san.simtaskclient.executor.SimTaskExecutorUtil;
public class SimTaskAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SimTaskAutoConfiguration.class);
    @Value("${spring.application.name:''}")
    private String appName;
    @Value("${sim-task.server-addr:''}")
    private String serverAddr;
    @Value("${server.port:8080}")
    private int appPort;
    @Value("${sim-task.base-scan-packages:''}")
    private String[] baseScanPackages;
    @Value("${sim-task.store-type:''}")
    private String storeType;
    @Value("${sim-task.heartbeat-interval:10}")
    private int heartbeatInterval;

    public static String serverState = "";

    private SchedulerFactoryBean schedulerFactoryBean;


    /*public SimTaskAutoConfiguration(SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }*/

    /*   @Bean
       public AnnotationScannerConfiguration annotationScannerConfiguration() {
           return new AnnotationScannerConfiguration();
       }
   */
    @Autowired
    private JobFactory jobFactory;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setJobFactory(jobFactory);
        schedulerFactoryBean.setGlobalTriggerListeners(new SimTaskJobAndTriggerListener());
        schedulerFactoryBean.setGlobalJobListeners(new SimTaskJobAndTriggerListener());
        this.schedulerFactoryBean = schedulerFactoryBean;
        return schedulerFactoryBean;
    }

    @Bean
    public Object init() {
        List<SimTaskBean> simTaskBeans;
        if (isServerType()) {
            simTaskBeans = initTaskFromServer();
        } else {
            simTaskBeans = buildSimTaskFromClasses(scanAnnotation());
        }
        if (simTaskBeans != null) {
            cacheTask(simTaskBeans);
            SimTaskExecutorUtil.registerQuartz(schedulerFactoryBean, simTaskBeans);
        }
        return new Object();
    }

    private boolean isServerType() {
        return "server".equalsIgnoreCase(this.storeType);
    }

    private Set<Class<?>> scanAnnotation() {
        ConfigurationBuilder config = new ConfigurationBuilder();
        config.addScanners(new TypeAnnotationsScanner());
        config.forPackages("com.yao2san.simtaskclient.config");
        if (baseScanPackages != null)
            config.forPackages(baseScanPackages);

        else config.forPackages("");
        Reflections reflections = new Reflections(config);
        reflections.getConfiguration().getScanners().add(new MethodAnnotationsScanner());
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(SimTask.class);
        logger.info("job class scan finished ({} job in total)", classes.size());
        return classes;
    }

    private void cacheTask(List<SimTaskBean> simTaskBeans) {
        SimTaskExecutorUtil.cacheTask(simTaskBeans);
    }

    private List<SimTaskBean> getCacheTask() {
        return SimTaskExecutorUtil.getCacheTask();
    }

    private List<SimTaskBean> getTasksFromServer() {
        return SimTaskExecutorUtil.getTaskFromServer(serverAddr, appName);
    }

    private List<SimTaskBean> buildSimTaskFromClasses(Set<Class<?>> classes) {
        List<SimTaskBean> simTaskBeans = SimTaskExecutorUtil.buildSimTaskFromClasses(classes);
        return simTaskBeans.stream().peek(m -> {
            m.setAppName(appName);
            m.setAppAddr(SimTaskUtil.getLocalHost() + ":" + appPort);
        }).collect(Collectors.toList());
    }

    private List<SimTaskBean> initTaskFromServer() {
        List<SimTaskBean> formClass = buildSimTaskFromClasses(scanAnnotation());
        return SimTaskExecutorUtil.initTaskFromServer(formClass, serverAddr, appName);
    }
    /* *//**
     * 扫描定时任务注解
     *//*
    @Bean("scan-task")
    public Object scan() {
        logger.info("start scan sim task...");
        ConfigurationBuilder config = new ConfigurationBuilder();
        config.addScanners(new MethodAnnotationsScanner());
        config.forPackages("com.yao2san.simtaskclient.config");
        if (baseScanPackages != null)
            config.forPackages(baseScanPackages);

        else config.forPackages("");
        Reflections reflections = new Reflections(config);
        reflections.getConfiguration().getScanners().add(new MethodAnnotationsScanner());
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(SimTask.class);
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
        }
        logger.info("sim task scan finished ({} task in total)", simTaskBeans.size());
        if ("server".equalsIgnoreCase(storeType)) {
            boolean regSuccess = registerServer(simTaskBeans);
            boolean cacheSuccess = cacheSimTask();
            if (regSuccess && cacheSuccess) {
                registerTask(simTaskBeans);
            } else {
                logger.error("task register to server not success, the tasks will not run until server state is ok");
            }
        } else {
            registerTask(simTaskBeans);
        }

        timingCacheTask();
        return new Object();
    }

    *//**
     * 注册定时任务
     *//*
    private void registerTask(List<SimTaskBean> simTaskBeans) {
        SimTaskExecutorUtil.registerQuartz(schedulerFactoryBean, simTaskBeans);
        try {
            if (!schedulerFactoryBean.getScheduler().isStarted()) {
                schedulerFactoryBean.getScheduler().start();
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    */

    /**
     * 注册到服务端
     *//*
    private boolean registerServer(List<SimTaskBean> simTaskBeans) {
        if (serverAddr == null || "".equals(serverAddr)) {
            logger.warn("server address config is null, will not be register to server");
            return false;
        }
        logger.info("start register sim task...");
        String service = "http://" + serverAddr + "/simTaskServer/register";

        try {
            ResponseData result = RequestUtil.post(service, simTaskBeans, ResponseData.class);
            if (result.getCode() == 1) {
                logger.info("sim task register success ({} task in total)", simTaskBeans.size());
                return true;
            }
        } catch (Exception e) {
            logger.error("sim task register error, please check server state", e);
        }
        return false;
    }

*/
    private Object timingCacheTask() {
        if (!"server".equalsIgnoreCase(storeType)) {
            return new Object();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("serverAddr", serverAddr);
        params.put("appName", appName);
        SimTaskExecutors.add(schedulerFactoryBean, TimingCacheTask.class.getName(), "_CACHE_TASK", "_SYSTEM", "0/10 * * * * ?", params);
        try {
            if (!schedulerFactoryBean.getScheduler().isStarted()) {
                schedulerFactoryBean.getScheduler().start();
            }
            SimTaskExecutors.trigger(schedulerFactoryBean, "_CACHE_TASK", "_SYSTEM");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return new Object();
    }
    /*
     *//**
     * 启动心跳连接
     *//*
    @Bean
    public Object heartbeat() {
        if (!"server".equalsIgnoreCase(storeType)) {
            return new Object();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("serverAddr", serverAddr);
        params.put("appName", appName);
        params.put("appPort", appPort);
        SimTaskExecutors.add(schedulerFactoryBean, SimTaskHeartbeat.class.getName(), "_HEARBEAT", "_SYSTEM", "0/10 * * * * ?", params);
        try {
            if (!schedulerFactoryBean.getScheduler().isStarted()) {
                schedulerFactoryBean.getScheduler().start();
            }
            SimTaskExecutors.trigger(schedulerFactoryBean, "_HEARBEAT", "_SYSTEM", params);
            logger.info("sim task heartbeat start success");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return new Object();
    }
*/

}
