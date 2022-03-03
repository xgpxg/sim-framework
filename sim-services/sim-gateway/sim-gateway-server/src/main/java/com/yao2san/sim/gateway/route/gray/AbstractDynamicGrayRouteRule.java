package com.yao2san.sim.gateway.route.gray;

import com.netflix.zuul.context.RequestContext;
import com.yao2san.sim.gateway.config.Constant;
import com.yao2san.sim.gateway.config.GatewayProperties;
import com.yao2san.sim.gateway.route.core.AbstractRouteLocator;
import com.yao2san.sim.gateway.route.core.RouteHelper;
import com.yao2san.sim.gateway.utils.ServiceUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.web.util.UrlPathHelper;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("Duplicates")
public abstract class AbstractDynamicGrayRouteRule extends AbstractGrayRouteRule {

    protected AtomicReference<Map<String, GatewayProperties.GrayRoute>> grayRoutes = new AtomicReference<>();


    private GatewayProperties gatewayProperties;

    @Autowired
    public final void setGatewayProperties(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }

    public AbstractDynamicGrayRouteRule(AbstractRouteLocator routeLocator, UrlPathHelper urlPathHelper) {
        super(routeLocator, urlPathHelper);
    }

    @PostConstruct
    private void initRouteMap() {
        if (this.grayRoutes.get() == null) {
            this.grayRoutes.set(this.locateGrayRoutes());
        }
    }

    protected List<GrayRoute> getGrayRoute() {
        List<GrayRoute> grayRoutes = new ArrayList<>();
        for (Map.Entry<String, GatewayProperties.GrayRoute> entry : this.grayRoutes.get().entrySet()) {
            grayRoutes.add(this.getGrayRoute(entry.getKey(), entry.getValue()));
        }
        return grayRoutes;
    }


    @Override
    public Map<String, GatewayProperties.GrayRoute> locateGrayRoutes() {
        if (this.grayRoutes.get() != null) {
            this.grayRoutes.get().clear();
        }
        Map<String, GatewayProperties.GrayRoute> grayRouteMap = GrayRouteHelper.getAll();
        if (grayRouteMap == null) {
            return null;
        }
        Map<String, GatewayProperties.GrayRoute> routeMap = new LinkedHashMap<>();
        if(gatewayProperties.getGrayRoute()!=null){
            routeMap.putAll(gatewayProperties.getGrayRoute().getRoutes());
        }
        routeMap.putAll(grayRouteMap);
        if (this.grayRoutes.get() != null) {
            this.grayRoutes.get().putAll(grayRouteMap);
        }
        return routeMap;
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
    @SuppressWarnings("all")
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
    public List<GrayRoute> getAll() {
        return this.getGrayRoute();
    }

    @Override
    public void refresh() {
        Map<String, GatewayProperties.GrayRoute> grayRouteMap = this.locateGrayRoutes();
        for (Map.Entry<String, GatewayProperties.GrayRoute> entry : grayRouteMap.entrySet()) {
            versionHandler(entry.getValue());
        }
    }

    private void versionHandler(GatewayProperties.GrayRoute grayRoute) {
        Set<String> serviceInstances = grayRoute.getServiceInstances();
        ZuulProperties.ZuulRoute zuulRoute = RouteHelper.getDynamicRouteLocator().getZuulRoute(grayRoute.getPath());
        String serviceId = zuulRoute.getServiceId();
        for (String serviceInstance : serviceInstances) {
            ServiceUtil.upsertMetadata(serviceId, serviceInstance, Constant.GRAY_STATUS_KEY, "1");
            ServiceUtil.upsertMetadata(serviceId, serviceInstance, Constant.VERSION_KEY, grayRoute.getGrayVersion());
        }

    }
}
