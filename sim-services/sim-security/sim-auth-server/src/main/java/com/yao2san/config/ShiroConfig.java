package com.yao2san.config;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class ShiroConfig {

    @Autowired
    private AppConfig appConfig;
    @Resource
    RedisSessionDAO redisSessionDAO;

    @Resource
    RedisCacheManager redisCacheManager;

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl(appConfig.getLoginUrl());

        /*Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        filters.put("roleFilter", new PermissionFilter());
        shiroFilterFactoryBean.setFilters(filters);*/

        shiroFilterFactoryBean.setUnauthorizedUrl(appConfig.getNotFoundUrl());

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/**/security/login", "anon");
        filterChainDefinitionMap.put("/**/security/logout", "anon");
        filterChainDefinitionMap.put("/**/user/token", "anon");

        filterChainDefinitionMap.put("/**/wx/**", "anon");


        filterChainDefinitionMap.put("/**/static/**", "anon");
        filterChainDefinitionMap.put("/**/404", "anon");
        filterChainDefinitionMap.put("/**/error", "anon");

        filterChainDefinitionMap.put("/**/oauth/authenticate", "anon");
        filterChainDefinitionMap.put("/**/oauth/token", "anon");

        filterChainDefinitionMap.put("/**", "authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;

    }
   /* @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        return chainDefinition;
    }*/
    @Bean
    public SessionsSecurityManager securityManager(SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //不要使用注入的bean,注入的bean被代理后名称不唯一 导致缓存中认证信息的key不一致
        securityManager.setRealm(new UserRealm());
        securityManager.setSessionManager(sessionManager);
        securityManager.setCacheManager(redisCacheManager);
        return securityManager;
    }

    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(-1);
        sessionManager.setSessionDAO(redisSessionDAO);
        return sessionManager;
    }


    @Bean
    public SimpleMappingExceptionResolver resolver() {
        SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
        Properties properties = new Properties();
        properties.setProperty("org.apache.shiro.authz.UnauthorizedException", "/403");
        resolver.setExceptionMappings(properties);
        return resolver;
    }


}
