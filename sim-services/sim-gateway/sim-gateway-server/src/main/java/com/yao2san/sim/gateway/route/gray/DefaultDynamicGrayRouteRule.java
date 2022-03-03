package com.yao2san.sim.gateway.route.gray;

import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import com.netflix.zuul.context.RequestContext;
import com.yao2san.sim.gateway.config.GatewayProperties;
import com.yao2san.sim.gateway.route.core.AbstractRouteLocator;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.web.util.UrlPathHelper;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("Duplicates")
public class DefaultDynamicGrayRouteRule extends AbstractDynamicGrayRouteRule {

    private final static String GRAY_ROUTE_RANDOM_KEY = "GRAY";
    private final static String MAIN_ROUTE_RANDOM_KEY = "MAIN";


    public DefaultDynamicGrayRouteRule(AbstractRouteLocator routeLocator, UrlPathHelper urlPathHelper) {
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
    @SuppressWarnings("Duplicates")
    public boolean isHit(GrayRoute grayRoute) {
        GrayRoute.RuleType ruleType = grayRoute.getRuleType();
        if (ruleType == GrayRoute.RuleType.WEIGHT_RANDOM) {
            return isHitForRandomWeight(grayRoute);
        }
        if (ruleType == GrayRoute.RuleType.HEADER) {
            return isHitForHeader(grayRoute);
        }

        return false;
    }

    private boolean isHitForRandomWeight(GrayRoute grayRoute) {
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

    private boolean isHitForHeader(GrayRoute grayRoute) {
        boolean isMatched = true;

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        Map<String, Object> routeHeaders = grayRoute.getHeaders();
        if (routeHeaders == null || routeHeaders.size() == 0) {
            return false;
        }
        for (Map.Entry<String, Object> entry : routeHeaders.entrySet()) {
            String value = request.getHeader(entry.getKey());
            if (value == null || !value.equals(String.valueOf(entry.getValue()))) {
                isMatched = false;
                break;
            }
        }
        return isMatched;
    }

    //TODO
    private boolean isHitForParams(GrayRoute grayRoute) {
        return false;
    }
    //TODO
    private boolean isHitForCookie(GrayRoute grayRoute) {
        return false;
    }

    @Override
    public List<GrayRoute> getAll() {
        return this.getGrayRoute();
    }


}
