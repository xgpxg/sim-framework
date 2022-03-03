package com.yao2san.sim.gateway.route.gray;

import com.netflix.zuul.context.RequestContext;
import com.yao2san.sim.gateway.config.GatewayProperties;
import com.yao2san.sim.gateway.route.core.AbstractRouteLocator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.web.util.UrlPathHelper;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class HeaderMatcherGrayRouteRule implements IGrayRouteRule, IGrayRouteLocator {

    private GatewayProperties properties;

    private AtomicReference<Map<String, GatewayProperties.GrayRoute>> grayRoutes = new AtomicReference<>();

    @Autowired
    private AbstractRouteLocator routeLocator;

    @Autowired
    private UrlPathHelper urlPathHelper;

    public HeaderMatcherGrayRouteRule(GatewayProperties properties) {
        this.properties = properties;
        this.initGrayRouteMap();
    }

    @SuppressWarnings("Duplicates")
    private List<GrayRoute> getGrayRoute() {
        List<GrayRoute> grayRoutes = new ArrayList<>();
        Map<String, GatewayProperties.GrayRoute> routeMap = this.properties.getGrayRoute().getRoutes();
        for (Map.Entry<String, GatewayProperties.GrayRoute> entry : routeMap.entrySet()) {
            grayRoutes.add(this.getGrayRoute(entry.getKey(), entry.getValue()));
        }
        return grayRoutes;
    }

    @PostConstruct
    private void initGrayRouteMap() {
        if (this.grayRoutes.get() == null) {
            this.grayRoutes.set(this.locateGrayRoutes());
        }
    }

    public Map<String, GatewayProperties.GrayRoute> locateGrayRoutes() {
        return this.properties.getGrayRoute().getRoutes();
    }

    private GrayRoute getGrayRoute(String id, GatewayProperties.GrayRoute grayRoute) {
        return new GrayRoute(id,
                grayRoute.getPath(),
                grayRoute.getGrayVersion(),
                grayRoute.getMainVersion(),
                grayRoute.getWeight(),
                grayRoute.getType(),
                grayRoute.getHeaders(),
                grayRoute.getParams(),
                grayRoute.getCookies());
    }

    @Override
    @SuppressWarnings("Duplicates")
    public GrayRoute matchingGrayRoute() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String path = urlPathHelper.getPathWithinApplication(request);
        ZuulProperties.ZuulRoute zuulRoute = routeLocator.getZuulRoute(path);

        List<GrayRoute> grayRoutes = this.getGrayRoute();

        AtomicReference<GrayRoute> matched = new AtomicReference<>();
        grayRoutes.forEach(grayRoute -> {
            if (zuulRoute != null) {
                if (StringUtils.equals(zuulRoute.getPath(), grayRoute.getPath())) {
                    matched.set(grayRoute);
                    return;
                }
            }
        });
        return matched.get();

    }

    @Override
    @SuppressWarnings("all")
    public boolean isHit(GrayRoute grayRoute) {
        boolean isMatched = true;

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        Map<String, Object> routeHeaders = grayRoute.getHeaders();
        if (routeHeaders == null || routeHeaders.size() == 0) {
            return false;
        }
        for (Map.Entry<String, Object> entry : routeHeaders.entrySet()) {
            String value = request.getHeader(entry.getKey());
            if (value == null || !value.equals(entry.getValue())) {
                isMatched = false;
                break;
            }
        }
        return isMatched;
    }

    @Override
    public List<GrayRoute> getAll() {
        return this.getGrayRoute();
    }
}
