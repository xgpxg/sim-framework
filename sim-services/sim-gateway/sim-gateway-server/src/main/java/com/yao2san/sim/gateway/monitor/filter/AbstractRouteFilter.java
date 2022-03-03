package com.yao2san.sim.gateway.monitor.filter;

import com.netflix.zuul.ZuulFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractRouteFilter extends ZuulFilter {

    public static final String ROUTE_METRIC_NAME_PREFIX = "ROUTE";
    public static final String GATEWAY_METRIC_NAME_PREFIX = "GATEWAY";
    public static final String DEFAULT_ID = "-1";

    @Value("${error.path:/error}")
    String errorPath;

    private final RouteLocator routeLocator;
    private final UrlPathHelper urlPathHelper;

    AbstractRouteFilter(RouteLocator routeLocator, UrlPathHelper urlPathHelper) {
        this.routeLocator = routeLocator;
        this.urlPathHelper = urlPathHelper;
    }

    protected Route getRoute(HttpServletRequest request) {
        String requestURI = urlPathHelper.getPathWithinApplication(request);
        return routeLocator.getMatchingRoute(requestURI);
    }
}
