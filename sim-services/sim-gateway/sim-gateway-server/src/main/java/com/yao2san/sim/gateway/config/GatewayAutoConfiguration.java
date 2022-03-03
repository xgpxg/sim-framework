package com.yao2san.sim.gateway.config;

import com.codahale.metrics.MetricRegistry;
import com.yao2san.sim.gateway.monitor.reporter.DatabaseReporter;
import com.yao2san.sim.gateway.route.core.AbstractRouteLocator;
import com.yao2san.sim.gateway.route.core.DatabaseRouteLocator;
import com.yao2san.sim.gateway.route.gray.DefaultDynamicGrayRouteRule;
import com.yao2san.sim.gateway.route.filter.VersionRuleFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UrlPathHelper;

@Configuration
public class GatewayAutoConfiguration {

    @Autowired
    private ZuulProperties zuulProperties;
    @Autowired
    private DispatcherServletPath dispatcherServletPath;

    @Bean
    public DatabaseRouteLocator databaseRouteLocator() {
        return new DatabaseRouteLocator(dispatcherServletPath.getPath(), zuulProperties);
    }

    @Bean
    public MetricRegistry metricRegistry() {
        return new MetricRegistry();
    }

    @Bean
    public DatabaseReporter databaseReporter(MetricRegistry metricRegistry) {
        return DatabaseReporter.forRegistry(metricRegistry).build();
    }


    @Bean
    public DefaultDynamicGrayRouteRule defaultDynamicGrayRouteRule(AbstractRouteLocator routeLocator, UrlPathHelper urlPathHelper) {
        return new DefaultDynamicGrayRouteRule(routeLocator, urlPathHelper);
    }

    @Bean
    @ConditionalOnProperty(prefix = "zuul.gray-route", name = "enable", havingValue = "true")
    public VersionRuleFilter metadataRuleFilter() {
        return new VersionRuleFilter();
    }
}
