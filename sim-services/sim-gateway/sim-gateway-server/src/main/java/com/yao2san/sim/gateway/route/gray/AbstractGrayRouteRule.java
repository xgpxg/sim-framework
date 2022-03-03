package com.yao2san.sim.gateway.route.gray;

import com.netflix.zuul.context.RequestContext;
import com.yao2san.sim.gateway.route.core.AbstractRouteLocator;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractGrayRouteRule implements IGrayRouteRule, IGrayRouteRefreshLocator {
     AtomicReference<List<GrayRoute>> grayRoutes = new AtomicReference<>();

    AbstractRouteLocator routeLocator;

    UrlPathHelper urlPathHelper;

    public AbstractGrayRouteRule(AbstractRouteLocator routeLocator, UrlPathHelper urlPathHelper) {
        this.routeLocator = routeLocator;
        this.urlPathHelper = urlPathHelper;
    }

    @Override
    @SuppressWarnings("all")
    public GrayRoute matchingGrayRoute() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String path = urlPathHelper.getPathWithinApplication(request);
        ZuulProperties.ZuulRoute zuulRoute = routeLocator.getZuulRoute(path);

        List<GrayRoute> grayRoutes = this.grayRoutes.get();

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
    public List<GrayRoute> getAll() {
        return this.grayRoutes.get();
    }

    public void add(GrayRoute grayRoute) {
        this.grayRoutes.get().add(grayRoute);
    }

    public void remove(GrayRoute grayRoute) {
        this.grayRoutes.get().remove(grayRoute);
    }

}
