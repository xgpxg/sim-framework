package com.yao2san.simapiserver.api.controller;

import com.yao2san.simapiclient.core.util.RestTemplateUtil;
import com.yao2san.simapiserver.api.bean.ApiQueryBean;
import com.yao2san.simapiserver.api.service.SimApi;
import com.yao2san.simapiserver.server.bean.ApiInfoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.yao2san.sim.framework.web.response.ResponseData;

import java.util.Map;

@RestController
@RequestMapping("api")
public class ApiController {
    @Autowired
    private SimApi simApi;
    @PostMapping("/api-list")
    public ResponseData simApiList(@RequestBody ApiQueryBean apiQueryBean){
        return ResponseData.success(simApi.simApiList(apiQueryBean));
    }

    @PostMapping("/open-close-simulation")
    public ResponseData openSimulation(Integer apiId, Integer open){
        if(simApi.openOrCloseSimulation(apiId,open)){
            if(open==1)
                return ResponseData.success("模拟API开启成功");
            else
                return ResponseData.success("模拟API关闭成功");
        }else {
            return ResponseData.error("模拟API开启失败");
        }
    }
    @GetMapping("/app-list")
    public ResponseData appList(){
        return ResponseData.success(simApi.appList());
    }
    @PostMapping("/update-api")
    public ResponseData updateApi(@RequestBody ApiQueryBean apiQueryBean){
        if(simApi.updateApi(apiQueryBean)){
            return ResponseData.success();
        }else {
            return ResponseData.error("更新失败");
        }
    }
    @GetMapping("/status")
    public ResponseData status(String appAddr){
        String url = "http://"+appAddr+"/sim-api/status";
        try{
            String res = RestTemplateUtil.get(url, String.class).getBody();
            return ResponseData.success(res);
        }catch (Exception e){
            return ResponseData.success("0");
        }

    }
    /**
     * 查询API模拟数据(用于页面)
     */
    @GetMapping("/sim-api-data")
    public Object simApiData(ApiInfoBean apiInfoBean){
        return ResponseData.success(simApi.simApiData(apiInfoBean));
    }

    @PostMapping("/add-api")
    public ResponseData addApi(@RequestBody Map params){
        return simApi.addApi(params);
    }

    @PostMapping("/delete-api")
    public ResponseData deleteApi(@RequestBody Map params){
        return simApi.deleteApi(params);
    }
}
