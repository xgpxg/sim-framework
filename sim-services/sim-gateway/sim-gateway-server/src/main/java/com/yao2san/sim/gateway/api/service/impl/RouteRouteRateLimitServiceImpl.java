package com.yao2san.sim.gateway.api.service.impl;

import com.github.pagehelper.PageInfo;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import com.yao2san.sim.gateway.api.request.RouteRateLimitReq;
import com.yao2san.sim.gateway.api.response.RouteRateLimitRes;
import com.yao2san.sim.gateway.api.service.RouteRateLimitService;
import com.yao2san.sim.gateway.rate.core.RateLimitHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RouteRouteRateLimitServiceImpl extends BaseServiceImpl implements RouteRateLimitService {

    @Override
    public void refresh() {
        RateLimitHelper.refresh();
    }

    @Override
    public void refresh(Long routeId) {
        RateLimitHelper.refresh(routeId);
    }

    @Override
    public RouteRateLimitRes detail(Long routeRateLimitPolicyId) {
        return sqlSession.selectOne("rateLimit.detail", routeRateLimitPolicyId);
    }

    @Override
    public PageInfo<RouteRateLimitRes> list(RouteRateLimitReq routeRateLimit) {
        return this.qryList("rateLimit.list", routeRateLimit);
    }

    @Override
    @Transactional
    public void add(RouteRateLimitReq routeRateLimit) {
        sqlSession.insert("rateLimit.add", routeRateLimit);
    }

    @Override
    @Transactional
    public void delete(Long rateLimitPolicyId) {
        sqlSession.delete("rateLimit.delete", rateLimitPolicyId);
    }

    @Override
    @Transactional
    public void update(RouteRateLimitReq routeRateLimit) {
        sqlSession.delete("rateLimit.update", routeRateLimit);
    }
}
