package com.yao2san.sim.gateway.route.core;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class RouteHelper {
    private static ApplicationEventPublisher eventPublisher;
    @Getter
    private static AbstractRouteLocator dynamicRouteLocator;

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        RouteHelper.eventPublisher = eventPublisher;
    }

    @Autowired
    public void setDynamicRouteLocator(AbstractRouteLocator dynamicRouteLocator) {
        RouteHelper.dynamicRouteLocator = dynamicRouteLocator;
    }

    public static void refresh() {
        RoutesRefreshedEvent routesRefreshedEvent = new RoutesRefreshedEvent(dynamicRouteLocator);
        RouteHelper.eventPublisher.publishEvent(routesRefreshedEvent);
    }

}
