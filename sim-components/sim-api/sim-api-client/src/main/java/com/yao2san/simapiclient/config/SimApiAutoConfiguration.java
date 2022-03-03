package com.yao2san.simapiclient.config;

import com.yao2san.simapiclient.core.util.RestTemplateUtil;
import com.yao2san.simapiclient.core.util.SimApiUtil;
import com.yao2san.simapiclient.register.bean.ApiMethod;
import com.yao2san.simapiclient.register.bean.ApiMethodParameter;
import com.yao2san.simapiclient.register.bean.ApiRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Configuration
public class SimApiAutoConfiguration {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${sim-api.server.addr:}")
    private String serverAddr;
    @Value("${server.port:0}")
    private int appPort;
    @Value("${sim-api.client.host:}")
    private String clientHost;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private static final Logger logger = LoggerFactory.getLogger(SimApiAutoConfiguration.class);

    @Bean
    @ConditionalOnProperty(prefix = "sim-api", name = "enabled", havingValue = "true")
    public SimApiServerConfig simApiServerConfig() {
        logger.info("sim-api enabled");
        return new SimApiServerConfig();
    }

    @Bean
    @ConditionalOnProperty(prefix = "sim-api", name = "enabled", havingValue = "true")
    public SimApiClientConfig simApiClientConfig() {
        return new SimApiClientConfig();
    }

    @Bean
    @ConditionalOnProperty(prefix = "sim-api", name = "enabled", havingValue = "true")
    public void init() {
        new Thread(() -> {
            logger.info("start scan api");
            List<ApiRegister> apiRegisters = new ArrayList<>();
            RequestMappingHandlerMapping mapping = webApplicationContext
                    .getBean(RequestMappingHandlerMapping.class);

            Map<RequestMappingInfo, HandlerMethod> map = mapping
                    .getHandlerMethods();
            for (RequestMappingInfo info : map.keySet()) {
                HandlerMethod handlerMethod = map.get(info);
                Method method = handlerMethod.getMethod();
                ApiMethod apiMethod = new ApiMethod();
                apiMethod.setMethodName(method.getName());
                apiMethod.setClassName(method.getDeclaringClass().getName());

                Parameter[] parameters = handlerMethod.getMethod().getParameters();
                List<ApiMethodParameter> apiMethodParameters = new ArrayList<>();
                int paramIndex = 0;
                for (Parameter parameter : parameters) {
                    ApiMethodParameter apiMethodParameter = new ApiMethodParameter();
                    apiMethodParameter.setIndex(paramIndex++);
                    apiMethodParameter.setName(parameter.getName());
                    apiMethodParameter.setType(parameter.getType().getName());
                    apiMethodParameters.add(apiMethodParameter);
                }
                apiMethod.setParameters(apiMethodParameters);

                Set<String> patterns = info.getPatternsCondition().getPatterns();
                Set<String> urls = new HashSet<>();
                Iterator<String> iterator = patterns.iterator();
                while (iterator.hasNext()) {
                    String url = iterator.next();
                    if (!url.startsWith("/sim-api")) {
                        urls.add(url);
                    }
                }
                if (urls.size() == 0)
                    continue;

                ApiRegister apiRegister = new ApiRegister();
                apiRegister.setAppName(appName);
                apiRegister.setApiName(info.getName());
                apiRegister.setMethod(apiMethod);
                apiRegister.setUrls(urls);
                apiRegister.setRequestMethods(info.getMethodsCondition().getMethods());
                apiRegister.setReturnType(method.getReturnType().getName());
                apiRegister.setAppAddr(SimApiUtil.getLocalHost() + ":" + appPort);
                if (clientHost != null && !"".equals(clientHost)) {
                    apiRegister.setAppAddr(clientHost + ":" + appPort);
                }
                apiRegister.setStatus("1000");
                apiRegisters.add(apiRegister);

                logger.info("sim-api : find api {}", urls);
            }
            logger.info("find total {} api", apiRegisters.size());
            String url = "http://" + serverAddr + "/sim-api/register";
            logger.info("start register api,server address : {}", url);
            try {
                ResponseEntity<Boolean> responseEntity = RestTemplateUtil.post(url, apiRegisters, Boolean.class);
                if (!responseEntity.getBody()) {
                    logger.error("sim-api registration failed");
                } else {
                    logger.info("sim-api registration success");
                }
            } catch (Exception e) {
                logger.error("sim-api registration error : {}", e.getMessage());
                e.printStackTrace();
            }

        }, "").start();

    }
}
