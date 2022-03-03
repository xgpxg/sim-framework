package com.yao2san.simapiclient.register.refresh;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.simapiclient.config.SimApiServerConfig;
import com.yao2san.simapiclient.core.util.CacheUtil;
import com.yao2san.simapiclient.core.util.RestTemplateUtil;
import com.yao2san.simapiclient.core.util.SimApiUtil;
import com.yao2san.simapiclient.register.bean.SimApiSimpleInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
@ConditionalOnProperty(prefix = "sim-api", name = "enabled", havingValue = "true")
@RestController
@RequestMapping("sim-api")
public class SImAPiRefresh {
    private static final Logger logger = LoggerFactory.getLogger(SImAPiRefresh.class);
    @Autowired
    private SimApiServerConfig simApiServerConfig;
    @Value("${spring.application.name:''}")
    private String appName;
    @Value("${server.port:0}")
    private int appPort;

    /**
     * 刷新api列表,用于服务端通知调用
     */
    @PostMapping("/refresh")
    @SuppressWarnings("all")
    public String refresh() {
        //服务端地址
        String serverAddr = simApiServerConfig.getAddr();
        String service = "http://" + serverAddr + "/sim-api/sim-api-list";
        //客户端地址
        String appAddr = SimApiUtil.getLocalHost() + ":" + appPort;
        List<SimApiSimpleInfo> simApiList = null;
        Map<String, Object> param = new HashMap<>();
        param.put("appAddr", appAddr);

        String reqUrl = SimApiUtil.buildUrl(service, param);
        //获取API列表
        String res = RestTemplateUtil.get(reqUrl, String.class).getBody();
        ResponseData<List<SimApiSimpleInfo>> responseData = JSONObject.parseObject(res, new TypeReference<ResponseData<List<SimApiSimpleInfo>>>() {
        });
        if (responseData != null) {
            simApiList = responseData.getData();
            //缓存数据
            CacheUtil.put(appName, simApiList, 1);
            logger.info("sim-api list refresh and cached : {}", res);
            return "1";
        } else {
            logger.error("sim-api list refresh failed : {}", res);
            return "0";
        }
    }

    @GetMapping("/status")
    public String status() {
        return "1";
    }
}
