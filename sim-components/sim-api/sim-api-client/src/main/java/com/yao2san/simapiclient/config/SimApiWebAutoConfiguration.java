package com.yao2san.simapiclient.config;

import com.yao2san.simapiclient.core.util.SimApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnProperty(prefix = "sim-api", name = "enabled", havingValue = "true")
@AutoConfigureAfter(SimApiAutoConfiguration.class)
public class SimApiWebAutoConfiguration implements WebMvcConfigurer {
    @Autowired
    private SimApiServerConfig simApiServerConfig;
    @Value("${spring.application.name:''}")
    private String appName;
    @Value("${server.port:0}")
    private int appPort;
    @Value("${sim-api.client.host:\"\"}")
    private String clientHost;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        SimApiInterceptor simApiInterceptor = new SimApiInterceptor(simApiServerConfig);
        simApiInterceptor.setAppName(appName);
        simApiInterceptor.setAppAddr(SimApiUtil.getLocalHost() + ":" + appPort);
        if (clientHost != null && !"".equals(clientHost)) {
            simApiInterceptor.setAppAddr(clientHost + ":" + appPort);
        }
        registry.addInterceptor(simApiInterceptor)
                .addPathPatterns("/**").excludePathPatterns("/sim-api/**", "/error");
    }

}
