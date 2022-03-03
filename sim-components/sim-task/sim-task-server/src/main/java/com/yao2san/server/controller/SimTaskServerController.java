package com.yao2san.server.controller;

import com.yao2san.server.bean.LogMsg;
import com.yao2san.server.bean.SimTaskBean;
import com.yao2san.server.service.SimTaskServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yao2san.sim.framework.web.response.ResponseData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("simTaskServer")
public class SimTaskServerController {
    @Autowired
    private SimTaskServerService simTaskServerService;

    @PostMapping("state")
    public String state(@RequestBody Map<String, Object> params) {
        return "ok";
    }

    @PostMapping("register")
    public ResponseData register(@RequestBody List<SimTaskBean> simTaskBeans) {
        return ResponseData.success(simTaskServerService.register(simTaskBeans));
    }

    @PostMapping("simTaskList")
    public ResponseData simTaskList(@RequestBody Map<String, Object> params) {
        SimTaskBean simTaskBean = new SimTaskBean();
        simTaskBean.setClassName("com.yao2san.simtaskclient.test.MainTest");
        simTaskBean.setTaskName("测测把");
        simTaskBean.setCron("0/1 * * * * ? ");
        simTaskBean.setGroup("test");
        simTaskBean.setEnabled(true);
        List<SimTaskBean> list = new ArrayList<>();
        list.add(simTaskBean);
        return ResponseData.success(list);
    }

    @PostMapping("log")
    public ResponseData log(@RequestBody LogMsg logMsg) {
        simTaskServerService.saveLog(logMsg);
        return ResponseData.success();
    }

}
