package com.yao2san.sim.gateway.scheduling;

import com.yao2san.sim.gateway.route.core.RouteHelper;
import com.yao2san.sim.gateway.route.gray.GrayRouteHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RouteScheduling {
    @Scheduled(fixedRateString = "#{${zuul.dynamic-route.refresh-interval-seconds:30}*1000}")
    private void refreshRoute() {
        RouteHelper.refresh();
        log.info("RouteScheduling: refresh route success!");
    }

    @Scheduled(fixedRateString = "#{${zuul.dynamic-route.refresh-interval-seconds:30}*1000}")
    private void refreshGrayRoute() {
        GrayRouteHelper.refresh();
        log.info("RouteScheduling: refresh gray route success!");
    }
}
