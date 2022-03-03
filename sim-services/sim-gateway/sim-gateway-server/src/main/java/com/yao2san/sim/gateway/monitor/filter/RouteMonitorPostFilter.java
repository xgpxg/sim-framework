package com.yao2san.sim.gateway.monitor.filter;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.netflix.zuul.context.RequestContext;
import com.yao2san.sim.gateway.config.GatewayProperties;
import com.yao2san.sim.gateway.monitor.reporter.MetricsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

@Component
public class RouteMonitorPostFilter extends AbstractRouteFilter {

    @Autowired
    private MetricRegistry metricRegistry;
    @Autowired
    private GatewayProperties gatewayProperties;

    public RouteMonitorPostFilter(RouteLocator routeLocator, UrlPathHelper urlPathHelper) {
        super(routeLocator, urlPathHelper);
    }

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return gatewayProperties.getReporter().isEnabled() && !ctx.getRequest().getRequestURI().equals(errorPath);
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();

        Route route = getRoute(ctx.getRequest());
        int status = ctx.getResponseStatusCode();

        //for global gateway
        count(status);
        count(route, status);

        //for special route
        time();
        time(route);

        return null;
    }

    private void count(Route route, int status) {

        String name;

        if (status >= 400) {
            name = MetricRegistry.name(ROUTE_METRIC_NAME_PREFIX, MetricsType.ERROR_COUNT.name(), route.getId());
        } else {
            name = MetricRegistry.name(ROUTE_METRIC_NAME_PREFIX, MetricsType.SUCCESS_COUNT.name(), route.getId());
        }

        Counter counter = metricRegistry.counter(name);
        counter.inc();
    }

    private void time(Route route) {

        String name = MetricRegistry.name(ROUTE_METRIC_NAME_PREFIX, MetricsType.TIME_AND_RATE.name(), route.getId());

        Timer timer = metricRegistry.timer(name);

        timer.time().stop();
    }

    private void count(int status) {

        String name;

        if (status >= 400) {
            name = MetricRegistry.name(GATEWAY_METRIC_NAME_PREFIX, MetricsType.ERROR_COUNT.name(), DEFAULT_ID);
        } else {
            name = MetricRegistry.name(GATEWAY_METRIC_NAME_PREFIX, MetricsType.SUCCESS_COUNT.name(), DEFAULT_ID);
        }

        Counter counter = metricRegistry.counter(name);
        counter.inc();
    }

    private void time() {
        String name = MetricRegistry.name(GATEWAY_METRIC_NAME_PREFIX, MetricsType.TIME_AND_RATE.name(), DEFAULT_ID);
        Timer timer = metricRegistry.timer(name);
        timer.time().stop();
    }
}
