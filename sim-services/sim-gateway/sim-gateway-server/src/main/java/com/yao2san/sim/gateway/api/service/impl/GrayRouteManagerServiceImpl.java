package com.yao2san.sim.gateway.api.service.impl;

import com.github.pagehelper.PageInfo;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import com.yao2san.sim.gateway.api.request.GrayRouteReq;
import com.yao2san.sim.gateway.api.request.RouteReq;
import com.yao2san.sim.gateway.api.response.GrayRouteRes;
import com.yao2san.sim.gateway.api.response.RouteRes;
import com.yao2san.sim.gateway.api.service.GrayRouteManagerService;
import com.yao2san.sim.gateway.api.service.RouteManagerService;
import com.yao2san.sim.gateway.config.Constant;
import com.yao2san.sim.gateway.route.gray.GrayRouteHelper;
import com.yao2san.sim.gateway.utils.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class GrayRouteManagerServiceImpl extends BaseServiceImpl implements GrayRouteManagerService {
    @Autowired
    private RouteManagerService routeManagerService;

    @Override
    public GrayRouteRes detail(Long routeGrayId) {
        return this.sqlSession.selectOne("grayRoute.detail", routeGrayId);
    }

    @Override
    public PageInfo<RouteRes> list(GrayRouteReq grayRouteReq) {
        return this.qryList("grayRoute.list", grayRouteReq);
    }

    @Override
    @Transactional
    public void add(GrayRouteReq grayRouteReq) {
        grayRouteReq.setStatus("1200");
        this.sqlSession.insert("grayRoute.add", grayRouteReq);
    }

    @Override
    public void delete(Long routeGrayId) {
        this.sqlSession.delete("grayRoute.delete", routeGrayId);
    }

    @Override
    @Transactional
    public void update(GrayRouteReq grayRouteReq) {
        this.sqlSession.update("grayRoute.update", grayRouteReq);
    }

    @Override
    @Transactional
    public void online(Long routeGrayId) {
        //update service instances metadata
        GrayRouteRes grayRouteRes = this.detail(routeGrayId);
        Set<String> serviceInstances = grayRouteRes.getServiceInstances();
        if (serviceInstances != null) {
            for (String serviceInstance : serviceInstances) {
                ServiceUtil.upsertMetadata(
                        grayRouteRes.getServiceId(),
                        serviceInstance,
                        Constant.VERSION_KEY,
                        grayRouteRes.getGrayVersion());
                ServiceUtil.upsertMetadata(
                        grayRouteRes.getServiceId(),
                        serviceInstance,
                        Constant.GRAY_STATUS_KEY,
                        "1");
            }
        }
        //update status
        GrayRouteReq req = new GrayRouteReq();
        req.setRouteGrayId(routeGrayId);
        req.setStatus("1000");
        this.update(req);

        GrayRouteHelper.refresh();

    }

    @Override
    @Transactional
    public void offline(Long routeGrayId) {
        //update service instances metadata
        GrayRouteRes grayRouteRes = this.detail(routeGrayId);
        RouteRes routeRes = routeManagerService.detail(grayRouteRes.getRouteId());
        Set<String> serviceInstances = grayRouteRes.getServiceInstances();
        if (serviceInstances != null) {
            for (String serviceInstance : serviceInstances) {
                ServiceUtil.upsertMetadata(
                        grayRouteRes.getServiceId(),
                        serviceInstance,
                        Constant.GRAY_STATUS_KEY,
                        "-1");
            }
        }
        //update status
        GrayRouteReq req = new GrayRouteReq();
        req.setRouteGrayId(routeGrayId);
        req.setStatus("1300");
        this.update(req);

        GrayRouteHelper.refresh();
    }

    @Override
    @Transactional
    public void formal(Long routeGrayId) {
        GrayRouteReq grayRoute = new GrayRouteReq();
        grayRoute.setRouteGrayId(routeGrayId);
        GrayRouteRes grayRouteRes = this.detail(routeGrayId);

        //update service instance
        Set<String> serviceInstances = grayRouteRes.getServiceInstances();
        for (String serviceInstance : serviceInstances) {
            ServiceUtil.upsertMetadata(
                    grayRouteRes.getServiceId(),
                    serviceInstance,
                    Constant.GRAY_STATUS_KEY,
                    "0");
            ServiceUtil.upsertMetadata(
                    grayRouteRes.getServiceId(),
                    serviceInstance,
                    Constant.VERSION_KEY,
                    grayRouteRes.getMainVersion());
        }

        // update route.version to current gray version
        /*RouteReq routeReq = new RouteReq();
        routeReq.setRouteId(grayRouteRes.getRouteId());
        routeReq.setVersion(grayRouteRes.getMainVersion());
        routeManagerService.update(routeReq);*/

        //update gray route to formal
        grayRoute.setStatus("1400");
        this.update(grayRoute);

        //refresh gray routes
        GrayRouteHelper.refresh();
    }
}
