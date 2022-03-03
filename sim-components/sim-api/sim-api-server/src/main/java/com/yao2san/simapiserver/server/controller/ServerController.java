package com.yao2san.simapiserver.server.controller;


import com.alibaba.fastjson.JSONArray;
import com.yao2san.simapiclient.register.bean.ApiRegister;
import com.yao2san.simapiserver.server.bean.ApiInfoBean;
import com.yao2san.simapiserver.server.service.ApiServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yao2san.sim.framework.web.response.ResponseData;

import java.net.URLDecoder;
import java.util.List;

@RestController
@RequestMapping("sim-api")
public class ServerController {
    private final static Logger logger = LoggerFactory.getLogger(ServerController.class);
    @Autowired
    private ApiServer apiServer;

    /**
     * 注册API
     *
     * @param apiRegisters
     */
    @RequestMapping("/register")
    public boolean register(@RequestBody List<ApiRegister> apiRegisters) {
        logger.info("receive register request , apis : {}", JSONArray.toJSONString(apiRegisters));
        apiServer.addApi(apiRegisters);
        logger.info("register success");
        return true;
    }

    @GetMapping("/sim-api-list")
    public ResponseData simApiList(ApiInfoBean apiInfoBean) {
        return ResponseData.success(apiServer.simApiList(apiInfoBean));
    }

    /**
     * 查询API模拟数据
     */
    @GetMapping("/sim-api-data")
    public Object simApiData(ApiInfoBean apiInfoBean) {
        //url含{}时被转码,进行还原
        apiInfoBean.setApiUrl(URLDecoder.decode(apiInfoBean.getApiUrl()));
        return apiServer.simApiData(apiInfoBean);
    }

   /* @PostMapping("/open-close-simulation")
    public ResponseData openSimulation(Integer apiId,Integer open){
        if(apiServer.openOrCloseSimulation(apiId,open)==1){
            if(open==1)
            return ResponseData.success("模拟API开启成功");
            else
            return ResponseData.success("模拟API关闭成功");
        }else {
            return ResponseData.error("模拟API开启失败");
        }
    }*/
}
