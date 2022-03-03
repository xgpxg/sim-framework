package com.yao2san.simapiclient.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.simapiclient.core.util.CacheUtil;
import com.yao2san.simapiclient.core.util.RestTemplateUtil;
import com.yao2san.simapiclient.core.util.SimApiUtil;
import com.yao2san.simapiclient.register.bean.SimApiSimpleInfo;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SimApiInterceptor implements HandlerInterceptor {
    private SimApiServerConfig simApiServerConfig;
    private String appName;
    private String appAddr;

    public SimApiInterceptor(SimApiServerConfig simApiServerConfig) {
        this.simApiServerConfig = simApiServerConfig;
    }

    private final static Logger logger = LoggerFactory.getLogger(SimApiInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String reqUrl = request.getRequestURI();
        String requestType = request.getMethod();
        if (isSimApi(reqUrl, requestType)) {
            String data = getSimApiData(appName, reqUrl, requestType);
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-type", "application/json;charset=utf-8");
            response.getWriter().println(data);
            return false;
        }
        return true;
    }

    /**
     * 是否是模拟API
     */
    private boolean isSimApi(String url, String requestType) {
        List<SimApiSimpleInfo> apiSimpleInfos = simApiList(appAddr);
        for (SimApiSimpleInfo info : apiSimpleInfos) {
            String apiUrl = info.getApiUrl();
            if ((url.equals(apiUrl) || isMatchUrl(apiUrl, url)) && requestType.equals(info.getReqMethod()) && info.getOpenSimulation() == 1) {
                return true;
            }
        }
        return false;
    }

    private static boolean isMatchUrl(String url, String target) {
        if (!url.contains("{")) {
            return false;
        }
        if (!url.endsWith("/"))
            url += "/";
        if (!target.endsWith("/"))
            target += "/";

        StringBuilder copyUrl = new StringBuilder(url);
        StringBuilder copyTarget = new StringBuilder(target);
        while (copyUrl.toString().contains("{")) {
            int start = copyUrl.indexOf("{");
            int end = copyUrl.indexOf("}");
            String value = copyTarget.substring(start, copyTarget.length());
            if ("".equals(value))
                break;
            value = value.substring(0, value.indexOf("/"));
            copyUrl.replace(start, end + 1, value);
        }
        return copyTarget.toString().equals(copyUrl.toString());
    }

    public static void main(String[] args) {
        System.out.println(isMatchUrl("/post/list/", "/post/1/"));
    }

    /**
     * 查询模拟API列表并缓存
     */
    private List<SimApiSimpleInfo> simApiList(String appAddr) {
        String service = "http://" + simApiServerConfig.getAddr() + "/sim-api/sim-api-list";

        List<SimApiSimpleInfo> simApiList = CacheUtil.get(appName);
        if (simApiList == null) {
            Map<String, Object> param = new HashMap<>();
            param.put("appAddr", appAddr);
            String reqUrl = SimApiUtil.buildUrl(service, param);
            String res = RestTemplateUtil.get(reqUrl, String.class).getBody();
            ResponseData<List<SimApiSimpleInfo>> responseData = JSONObject.parseObject(res, new TypeReference<ResponseData<List<SimApiSimpleInfo>>>() {
            });
            if (responseData != null) {
                simApiList = responseData.getData();
                CacheUtil.put(appName, simApiList, 60);
                logger.info("sim-api list loaded and cached : {}", res);
            } else {
                logger.error("sim-api list get failed : {}", res);
            }
        }
        return simApiList;
    }

    /**
     * 获取API模拟数据
     *
     * @param appName
     * @param url
     * @return
     */
    private String getSimApiData(String appName, String url, String requestType) {
        //处理url含{}时的情况
        List<SimApiSimpleInfo> apiSimpleInfos = simApiList(appAddr);
        for (SimApiSimpleInfo info : apiSimpleInfos) {
            String apiUrl = info.getApiUrl();
            if (isMatchUrl(apiUrl, url))
                url = apiUrl;
        }
        String service = "http://" + simApiServerConfig.getAddr() + "/sim-api/sim-api-data";
        Map<String, Object> param = new HashMap<>();
        param.put("appName", appName);
        param.put("apiUrl", url);
        param.put("appAddr", appAddr);
        param.put("requestType", requestType);
        service = SimApiUtil.buildUrl(service, param);
        String res = RestTemplateUtil.get(service, String.class, param).getBody();
        logger.info("simulation data : {}", res);

        return res;
    }


}
