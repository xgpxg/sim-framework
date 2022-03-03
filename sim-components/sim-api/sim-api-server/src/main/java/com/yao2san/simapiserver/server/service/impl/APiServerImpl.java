package com.yao2san.simapiserver.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yao2san.simapiclient.core.util.RestTemplateUtil;
import com.yao2san.simapiclient.register.bean.ApiRegister;
import com.yao2san.simapiclient.register.bean.SimApiSimpleInfo;
import com.yao2san.simapiserver.server.bean.ApiInfoBean;
import com.yao2san.simapiserver.server.service.ApiServer;
import org.apache.commons.collections4.MapUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class APiServerImpl implements ApiServer {
    private static final Logger logger = LoggerFactory.getLogger(APiServerImpl.class);
    @Autowired
    private SqlSessionTemplate session;

    @Override
    public void addApi(List<ApiRegister> apiRegisters) {
        //使得当前应用Api失效
        session.update("server.invalidAllApi", apiRegisters.get(0));
        Map<String, Object> params = new HashMap<>();
        apiRegisters.forEach(item -> {
            params.put("appName", item.getAppName());
            params.put("appAddr", item.getAppAddr());
            params.put("apiName", item.getApiName());
            params.put("apiDesc", item.getApiDesc());
            params.put("resType", item.getReturnType());
            params.put("status", item.getStatus());
            params.put("method", JSONObject.toJSONString(item.getMethod()));
            Set<String> urls = item.getUrls();
            urls.forEach(url -> {
                Set<RequestMethod> requestMethods = item.getRequestMethods();
                requestMethods.forEach(requestMethod -> {
                    params.put("apiUrl", url);
                    params.put("reqMethod", requestMethod.name());
                    int count = session.selectOne("server.checkExist", params);
                    if (count == 0)
                        session.insert("server.addApi", params);
                    else session.update("server.updateApi", params);
                });
            });
        });
    }

    @Override
    public List<SimApiSimpleInfo> simApiList(ApiInfoBean apiInfoBean) {
        return session.selectList("server.simApiList", apiInfoBean);
    }

    @Override
    public Object simApiData(ApiInfoBean apiInfoBean) {
        return session.selectOne("server.simApiData", apiInfoBean);
    }

    @Override
    public int openOrCloseSimulation(Integer apiId, Integer open) {
        Map<String, Object> params = new HashMap<>();
        params.put("apiId", apiId);
        params.put("open", open);

        int count = session.update("server.openOrCloseSimulation", params);
        //通知API所属应用刷新API列表缓存
        notifyRefresh(apiId);
        return count;
    }

    @Override
    public ApiInfoBean apiDetail(Integer apiId) {
        return session.selectOne("server.apiDetail", apiId);
    }

    @Override
    public void updateApi(Integer apiId) {

    }

    @Override
    public void deleteApi(Integer apiId) {

    }

    /**
     * 通知客户端刷新api列表(只刷新api归属应用的api列表)
     *
     * @param apiId apiId
     */
    private void notifyRefresh(Integer apiId) {
        ApiInfoBean apiInfoBean = apiDetail(apiId);
        String appAddr = apiInfoBean.getAppAddr();
        String url = "http://" + appAddr + "/sim-api/refresh";
        String res = RestTemplateUtil.post(url, String.class).getBody();
        logger.info(res);
    }

    /**
     * 通知客户端刷新api列表(刷新所有应用)
     */
    private void notifyRefreshAll() {
        List<Map<String, Object>> appList = session.selectList("server.appList");
        for (Map<String, Object> map : appList) {
            String appAddr = MapUtils.getString(map, "appAddr", "");
            if (!StringUtils.isEmpty(appAddr)) {
                String url = "http://" + appAddr + "/sim-api/refresh";
                String res = RestTemplateUtil.post(url, String.class).getBody();
                logger.info(res);
            }
        }
    }
}
