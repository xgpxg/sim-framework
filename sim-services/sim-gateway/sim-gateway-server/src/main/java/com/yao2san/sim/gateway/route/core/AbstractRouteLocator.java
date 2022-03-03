package com.yao2san.sim.gateway.route.core;

import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dynamic router loader. You need implement method
 * <code>loadDynamicRoute()</code>
 * <p>
 * You can configure routers in properties, databases, nacos ...
 *
 * @author wxg
 */
public abstract class AbstractRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator {

    public AbstractRouteLocator(String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
    }

    @Override
    public void refresh() {
        super.doRefresh();
    }

    @Override
    protected synchronized Map<String, ZuulProperties.ZuulRoute> locateRoutes() {
        Map<String, ZuulProperties.ZuulRoute> routes = new LinkedHashMap<>();
        Map<String, ZuulProperties.ZuulRoute> dynamicRoute = this.loadDynamicRoute();
        routes.putAll(super.locateRoutes());
        routes.putAll(dynamicRoute);
        return routes;
    }

    /**
     * Implement a custom method of obtaining routing configuration.
     *
     * @return Return the router config.
     */
    abstract Map<String, ZuulProperties.ZuulRoute> loadDynamicRoute();


    @Override
    public ZuulProperties.ZuulRoute getZuulRoute(String adjustedPath) {
        return super.getZuulRoute(adjustedPath);
    }
}
