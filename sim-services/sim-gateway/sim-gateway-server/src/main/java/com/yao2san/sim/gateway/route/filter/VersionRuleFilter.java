package com.yao2san.sim.gateway.route.filter;

import com.netflix.zuul.ZuulFilter;
import com.yao2san.sim.gateway.config.Constant;
import com.yao2san.sim.gateway.config.GatewayProperties;
import com.yao2san.sim.gateway.route.gray.GrayRoute;
import com.yao2san.sim.gateway.route.gray.IGrayRouteRule;
import io.jmnarloch.spring.cloud.ribbon.support.RibbonFilterContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 *
 */
@Slf4j
public class VersionRuleFilter extends ZuulFilter {

    @Autowired
    private GatewayProperties properties;
    @Autowired
    private IGrayRouteRule grayRouteRule;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        GrayRoute grayRoute = grayRouteRule.matchingGrayRoute();

        //clean current context attrs
        RibbonFilterContextHolder.clearCurrentContext();

        if (grayRoute != null) {
            if (grayRouteRule.isHit(grayRoute)) {
                //if hit gray route, set grayStatus=1, version=gray version
                log.debug("hit gray route:{}", grayRoute);
                RibbonFilterContextHolder.getCurrentContext().add(Constant.GRAY_STATUS_KEY, "1");
                RibbonFilterContextHolder.getCurrentContext().add(Constant.VERSION_KEY, grayRoute.getGrayVersion());
            } else {
                //if not hit, just set grayStatus=0
                RibbonFilterContextHolder.getCurrentContext().add(Constant.GRAY_STATUS_KEY, "0");
            }
        } else {
            RibbonFilterContextHolder.getCurrentContext().add(Constant.GRAY_STATUS_KEY, "0");
        }

        return null;
    }
}
