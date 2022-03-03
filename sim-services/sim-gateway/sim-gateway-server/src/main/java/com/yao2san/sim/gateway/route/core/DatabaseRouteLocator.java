package com.yao2san.sim.gateway.route.core;

import com.yao2san.sim.gateway.config.Constant;
import com.yao2san.sim.gateway.utils.ServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Load router from database.
 *
 * @author wxg
 */
@Slf4j
@DependsOn("serviceUtil")
public class DatabaseRouteLocator extends AbstractRouteLocator {

    private List<DynamicRoute> dbRoutes;

    private static boolean isFirstLoad = true;

    @Autowired
    private SqlSession sqlSession;

    public DatabaseRouteLocator(String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
    }

    @Override
    synchronized Map<String, ZuulProperties.ZuulRoute> loadDynamicRoute() {
        Map<String, ZuulProperties.ZuulRoute> routeMap = new LinkedHashMap<>();

        this.dbRoutes = sqlSession.selectList("route.loadAll");

        if (this.dbRoutes.size() > 0) {
            this.dbRoutes.forEach(route -> {
                routeMap.put(route.getPath(), route);
                versionHandler(route);
            });
        } else {
            log.info("Dynamic route not found.");
        }
        return routeMap;
    }

    private void versionHandler(DynamicRoute route) {
        if (isFirstLoad
                && route.getServiceId() != null
                && !"".equals(route.getServiceId())) {
            ServiceUtil.upsertMetadata(route.getServiceId(), Constant.GRAY_STATUS_KEY, "0");
        }
        if (isFirstLoad
                && route.getServiceId() != null
                && !"".equals(route.getServiceId())
                && route.getVersion() != null
                && !"".equals(route.getVersion())) {
            ServiceUtil.upsertMetadata(route.getServiceId(), Constant.VERSION_KEY, route.getVersion());
            isFirstLoad = false;
        }
    }
    /**
     * this route matcher implement route version control.
     * it can be used AB test or gray release
     *//*
    @Override
    public Route getMatchingRoute(String path) {

        //get version from header
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String version = request.getHeader(KEY);
        //RibbonFilterContextHolder.getCurrentContext().add(KEY, version);

        log.debug("version:{}", version);

        //default matched route.
        Route matchingRoute = super.getMatchingRoute(path);

        if (version == null) {
            return matchingRoute;
        }

        //match route with version.

        //match location from service id. use special service id to identify the routing version.
        //like this: service-a-v-1-0-0, means service version is 1.0.0
        Map<String, ZuulProperties.ZuulRoute> zuulRouteMap = properties.getRoutes();
        String matchedId = zuulRouteMap.keySet().stream().filter(routeId -> {
            if (routeId.contains("-v-")) {
                String v = routeId.split("-v-")[1].replace("-", ".");
                ZuulProperties.ZuulRoute zuulRoute = getZuulRoute(path);
                if (zuulRoute.getPath().equals(zuulRouteMap.get(routeId).getPath())
                        && v.equals(version)) {
                    log.info("match version from service id: {}", zuulRouteMap.get(routeId));
                    return true;
                }
            }
            return false;
        }).findAny().orElse(null);
        String location = null;
        if (matchedId != null) {
            ZuulProperties.ZuulRoute zuulRoute = zuulRouteMap.get(matchedId);
            location = zuulRoute.getLocation();
        }

        //match location from database. parameters: path, version (note: use cache)
        //the config of database will be cover the properties or yml in program
        //test: if version=1.0,return http://localhost:8001,if version=2.0,return http://localhost:8002
        DynamicRoute dynamicRoute = this.dbRoutes.stream().filter(dbRoute -> {
            String v = dbRoute.getVersion();
            ZuulProperties.ZuulRoute zuulRoute = getZuulRoute(path);
            if (zuulRoute.getPath().equals(dbRoute.getPath())
                    && v != null
                    && v.equals(version)) {
                log.info("match version from service id: {}", zuulRouteMap.get(zuulRoute.getServiceId()));
                return true;
            }
            return false;
        }).findAny().orElse(null);

        if (dynamicRoute != null) {
            location = dynamicRoute.getLocation();
        }

        if (location != null && !"".equals(location)) {
            matchingRoute.setLocation(location);
        }
        //note: if no matched route, will return default matched route,
        // if have multiple version, return the lasted version route

        System.out.println(matchingRoute);
        return matchingRoute;
    }*/
}
