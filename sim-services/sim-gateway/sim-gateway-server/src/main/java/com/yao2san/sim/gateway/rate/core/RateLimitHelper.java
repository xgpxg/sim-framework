package com.yao2san.sim.gateway.rate.core;

import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.properties.RateLimitProperties;
import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.properties.RateLimitType;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class RateLimitHelper {

    private static RateLimitProperties rateLimitProperties;
    private static SqlSession sqlSession;

    @Autowired
    public void setRateLimitProperties(RateLimitProperties rateLimitProperties) {
        RateLimitHelper.rateLimitProperties = rateLimitProperties;
    }

    @Autowired
    public void setSqlSession(SqlSession sqlSession) {
        RateLimitHelper.sqlSession = sqlSession;
    }

    /**
     * refresh all rate limit
     */
    public static void refresh() {
        refresh(null);
    }

    /**
     * refresh rate limit
     */
    public static void refresh(Long routeId) {
        List<RouteRateLimit> list;
        if (routeId != null) {
            list = sqlSession.selectList("rateLimit.qryRouteRateLimits", new RouteRateLimit().setRouteId(routeId));
        } else {
            list = sqlSession.selectList("rateLimit.qryRouteRateLimits");
        }
        if (list != null) {
            Map<String, List<RateLimitProperties.Policy>> policyMap = new HashMap<>();
            list.forEach(routeRateLimit -> {
                RateLimitProperties.Policy policy = new RateLimitProperties.Policy();
                policy.setLimit(routeRateLimit.getLimit());
                policy.setQuota(routeRateLimit.getQuota());
                policy.setRefreshInterval(routeRateLimit.getRefreshInterval());
                List<RateLimitProperties.Policy.MatchType> types = new ArrayList<>();
                for (String type : routeRateLimit.getMatchType().split(",")) {
                    types.add(new RateLimitProperties.Policy.MatchType(RateLimitType.valueOf(RateLimitType.class, type), routeRateLimit.getMatcher()));
                }
                policy.setType(types);
                //NOTE: the key is Zuul.Route.id, not service id
                String key = routeRateLimit.getId();
                if (policyMap.get(key) == null) {
                    List<RateLimitProperties.Policy> li = new LinkedList<>();
                    li.add(policy);
                    policyMap.put(key, li);
                } else {
                    policyMap.get(key).add(policy);
                }
            });

            rateLimitProperties.setPolicyList(policyMap);

            log.info("Rate limit policy load success");
        } else {
            log.info("Not found any policies");
        }
    }
}
