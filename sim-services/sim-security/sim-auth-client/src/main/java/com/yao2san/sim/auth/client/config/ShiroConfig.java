package com.yao2san.sim.auth.client.config;

import com.yao2san.sim.auth.client.filter.PermissionFilter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
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

        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        //权限过滤
        filters.put("permissionFilter", new PermissionFilter());
        shiroFilterFactoryBean.setFilters(filters);

        shiroFilterFactoryBean.setUnauthorizedUrl(appConfig.getNotFoundUrl());

        Map<String, String> filterChainDefinitionMap = this.getFilterChainDefinitionMap(appConfig.getIsAuth());

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;

    }

    private Map<String, String> getFilterChainDefinitionMap(boolean isAuth) {
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        if (!isAuth) {
            filterChainDefinitionMap.put("/**", "anon");
            return filterChainDefinitionMap;
        }
        //对外接口 不做验证 在TokenFilter做校验
        filterChainDefinitionMap.put("/service/**", "anon");

        filterChainDefinitionMap.put("/**/security/login", "anon");
        filterChainDefinitionMap.put("/**/security/logout", "anon");
        filterChainDefinitionMap.put("/**/user/token", "anon");

        filterChainDefinitionMap.put("/**/wx/**", "anon");

        filterChainDefinitionMap.put("/**/static/**", "anon");
        filterChainDefinitionMap.put("/**/404", "anon");
        filterChainDefinitionMap.put("/**/error", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/**/*.js", "anon");
        filterChainDefinitionMap.put("/**/*.css", "anon");

        filterChainDefinitionMap.put("/**/oauth/authenticate", "anon");
        filterChainDefinitionMap.put("/**/oauth/token", "anon");

        filterChainDefinitionMap.put("/**", "authc,permissionFilter");

        return filterChainDefinitionMap;

    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        return chainDefinition;
    }

    @Bean
    public SessionsSecurityManager securityManager(SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
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
