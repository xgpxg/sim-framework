package com.yao2san.sim.gateway.route.gray;

import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.yao2san.sim.gateway.config.GatewayProperties;
import com.yao2san.sim.gateway.route.core.RouteHelper;
import com.yao2san.sim.gateway.utils.CacheUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@DependsOn({"routeHelper","serviceUtil"})
public class GrayRouteHelper {
    public static final String GRAY_ROUTE_CACHE_KEY = "GRAY_ROUTE";

    public static WeightRandom<String> random = RandomUtil.weightRandom(Collections.EMPTY_LIST);

    private static SqlSession sqlSession;


    private static IGrayRouteRefreshLocator grayRouteRefreshLocator;

    @Autowired
    public void setSqlSession(SqlSession sqlSession) {
        GrayRouteHelper.sqlSession = sqlSession;
    }

    @Autowired
    public void setGrayRouteRefreshLocator(IGrayRouteRefreshLocator locator) {
        GrayRouteHelper.grayRouteRefreshLocator = locator;
    }



    /**
     * set cache for gray route
     */
    @SuppressWarnings("all")
    private static void cacheAll() {
        List<Map<String, Object>> list = sqlSession.selectList("route.loadGrayRoutes");
        Map<String,GatewayProperties.GrayRoute> grayRoutes = new HashMap<>();
        list.forEach(item -> {
            GatewayProperties.GrayRoute grayRoute = new GatewayProperties.GrayRoute();
            grayRoute.setPath(MapUtils.getString(item, "path"));
            grayRoute.setPath(MapUtils.getString(item, "path"));
            grayRoute.setType(GrayRoute.RuleType.valueOf(MapUtils.getString(item, "type")));
            grayRoute.setMainVersion(MapUtils.getString(item, "mainVersion"));
            grayRoute.setGrayVersion(MapUtils.getString(item, "grayVersion"));
            grayRoute.setWeight(MapUtils.getDouble(item, "weight"));
            grayRoute.setHeaders(JSONObject.parseObject(MapUtils.getString(item, "headers"), Map.class));
            grayRoute.setParams(JSONObject.parseObject(MapUtils.getString(item, "params"), Map.class));
            grayRoute.setCookies(JSONObject.parseObject(MapUtils.getString(item, "cookies"), Map.class));
            grayRoute.setServiceInstances(Sets.newHashSet(MapUtils.getString(item, "serviceInstances").split(",")));
            grayRoutes.put(MapUtils.getString(item, "serviceId"),grayRoute);
        });
        CacheUtil.put(GRAY_ROUTE_CACHE_KEY, grayRoutes);
    }

    @SuppressWarnings("all")
    public static Map<String,GatewayProperties.GrayRoute> getAll() {
        Object value = CacheUtil.get(GRAY_ROUTE_CACHE_KEY);
        if(value instanceof Optional){
            return Collections.emptyMap();
        }
        Map<String,GatewayProperties.GrayRoute> routeMap = (Map<String,GatewayProperties.GrayRoute>)value ;
        return routeMap;
    }

    @PostConstruct
    public static void refresh() {
        cacheAll();
        grayRouteRefreshLocator.refresh();
    }


}
