package com.yao2san.sim.gateway.route.gray;

import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
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

public class WeightRandomGrayRouteRule implements IGrayRouteRule, IGrayRouteLocator {

    private GatewayProperties properties;

    private AtomicReference<Map<String, GatewayProperties.GrayRoute>> grayRoutes = new AtomicReference<>();

    private final static String GRAY_ROUTE_RANDOM_KEY = "GRAY";
    private final static String MAIN_ROUTE_RANDOM_KEY = "MAIN";
    @Autowired
    private AbstractRouteLocator routeLocator;

    @Autowired
    private UrlPathHelper urlPathHelper;

    public WeightRandomGrayRouteRule(GatewayProperties properties) {
        this.properties = properties;
    }


    public List<GrayRoute> getGrayRoute() {
        List<GrayRoute> grayRoutes = new ArrayList<>();
        Map<String, GatewayProperties.GrayRoute> routeMap = this.properties.getGrayRoute().getRoutes();
        for (Map.Entry<String, GatewayProperties.GrayRoute> entry : routeMap.entrySet()) {
            grayRoutes.add(this.getGrayRoute(entry.getKey(), entry.getValue()));
        }
        return grayRoutes;
    }

    @PostConstruct
    public Map<String, GatewayProperties.GrayRoute> getGrayRouteMap() {
        if (this.grayRoutes.get() == null) {
            this.grayRoutes.set(this.locateGrayRoutes());
        }
        return this.grayRoutes.get();
    }

    public Map<String, GatewayProperties.GrayRoute> locateGrayRoutes() {
        return this.properties.getGrayRoute().getRoutes();
    }

    public GrayRoute getGrayRoute(String id, GatewayProperties.GrayRoute grayRoute) {
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
    @SuppressWarnings("all")
    public boolean isHit(GrayRoute grayRoute) {
        List<WeightRandom.WeightObj<String>> weightObjs = new ArrayList<>();
        WeightRandom.WeightObj<String> grayWeightObj = new WeightRandom.WeightObj<>(
                GRAY_ROUTE_RANDOM_KEY,
                grayRoute.getWeight());
        WeightRandom.WeightObj<String> mainWeightObj = new WeightRandom.WeightObj<>(
                MAIN_ROUTE_RANDOM_KEY,
                100.0 - grayRoute.getWeight());
        weightObjs.add(grayWeightObj);
        weightObjs.add(mainWeightObj);
        WeightRandom<String> random = RandomUtil.weightRandom(weightObjs);
        return "GRAY".equals(random.next());
    }

    @Override
    public List<GrayRoute> getAll() {
        return this.getGrayRoute();
    }
}
