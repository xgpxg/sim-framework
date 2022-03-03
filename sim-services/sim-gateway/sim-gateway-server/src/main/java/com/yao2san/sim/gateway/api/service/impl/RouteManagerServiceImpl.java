package com.yao2san.sim.gateway.api.service.impl;

import com.github.pagehelper.PageInfo;
import com.yao2san.sim.framework.web.exception.BusiException;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import com.yao2san.sim.gateway.api.request.RouteReq;
import com.yao2san.sim.gateway.api.response.RouteRes;
import com.yao2san.sim.gateway.api.service.RouteManagerService;
import com.yao2san.sim.gateway.route.core.RouteHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
public class RouteManagerServiceImpl extends BaseServiceImpl implements RouteManagerService {

    @Override
    @Transactional
    public void add(RouteReq routeReq) {
        if (StringUtils.isEmpty(routeReq.getUrl()) && StringUtils.isEmpty(routeReq.getServiceId())) {
            throw new BusiException("url or serviceId cannot be empty at the same time");
        }

        if (exist(routeReq.getPath())) {
            throw new BusiException("path [" + routeReq.getPath() + "] already matches another route, please change then path value");
        }
        this.sqlSession.insert("route.add", routeReq);
        log.info("Add route:{}", routeReq);
    }

    private boolean exist(String path) {
        Integer count = this.sqlSession.selectOne("route.existByPath", path);
        return count > 0;
    }

    @Override
    public void delete(Long routeId) {
        this.sqlSession.insert("route.delete", routeId);
    }

    @Override
    public void update(RouteReq routeReq) {

        this.sqlSession.update("route.update", routeReq);

        //if has been online, refresh route
        RouteRes routeRes = this.sqlSession.selectOne("route.detail", routeReq.getRouteId());
        if (StringUtils.equals(routeRes.getStatus(), "1000")) {
            RouteHelper.refresh();
        }
    }

    @Override
    @Transactional
    public void online(Long routeId) {
        RouteReq routeReq = new RouteReq();
        routeReq.setRouteId(routeId);
        routeReq.setStatus("1000");
        this.update(routeReq);
        RouteHelper.refresh();
    }

    @Override
    @Transactional
    public void offline(Long routeId) {
        RouteReq routeReq = new RouteReq();
        routeReq.setRouteId(routeId);
        routeReq.setStatus("1300");
        this.update(routeReq);
        RouteHelper.refresh();
    }

    @Override
    public RouteRes detail(@NotNull Long routeId) {
        return this.sqlSession.selectOne("route.detail", routeId);
    }

    @Override
    public PageInfo<RouteRes> list(RouteReq routeReq) {
        return this.qryList("route.list", routeReq);
    }
}
