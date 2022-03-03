package com.yao2san.manager.controller;

import com.yao2san.manager.bean.SimTaskQueryBean;
import com.yao2san.manager.service.SimTaskManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yao2san.sim.framework.web.response.ResponseData;

@RestController
@RequestMapping("simTaskManager")
public class SimTaskManagerController {
    @Autowired
    private SimTaskManagerService managerService;

    @RequestMapping("resume")
    public ResponseData resume(@RequestBody SimTaskQueryBean simTaskQueryBean) {
        return managerService.resume(simTaskQueryBean);
    }

    @RequestMapping("suspend")
    public ResponseData suspend(@RequestBody SimTaskQueryBean simTaskQueryBean) {
        return managerService.suspend(simTaskQueryBean);

    }

    @RequestMapping("stop")
    public ResponseData stop(@RequestBody SimTaskQueryBean simTaskQueryBean) {
        return managerService.resume(simTaskQueryBean);

    }

    @RequestMapping("refresh")
    public ResponseData refresh(@RequestBody SimTaskQueryBean simTaskQueryBean) {
        return managerService.refresh(simTaskQueryBean);

    }

    @RequestMapping("trigger")
    public ResponseData trigger(@RequestBody SimTaskQueryBean simTaskQueryBean) {
        return managerService.trigger(simTaskQueryBean);

    }

    @RequestMapping("state")
    public ResponseData state(Integer taskId, String state) {
        SimTaskQueryBean simTaskQueryBean = new SimTaskQueryBean();
        simTaskQueryBean.setId(taskId);
        simTaskQueryBean.setState(state);
        switch (state) {
            case "suspend":
                return managerService.suspend(simTaskQueryBean);
            case "resume":
                return managerService.resume(simTaskQueryBean);
            case "stop":
                return managerService.stop(simTaskQueryBean);
            case "start":
                return managerService.start(simTaskQueryBean);
            case "trigger":
                return managerService.trigger(simTaskQueryBean);
        }
        return ResponseData.error("状态[" + state + "]不支持");
    }

    @RequestMapping("enabled")
    public ResponseData enabled(Integer taskId, Boolean enabled) {
        SimTaskQueryBean simTaskQueryBean = new SimTaskQueryBean();
        simTaskQueryBean.setId(taskId);
        simTaskQueryBean.setEnabled(enabled);
        return managerService.enabled(simTaskQueryBean);
    }

    @GetMapping("taskList")
    public ResponseData queryTaskList(SimTaskQueryBean simTaskQueryBean) {
        return managerService.queryTaskList(simTaskQueryBean);
    }

    @GetMapping("logList")
    public ResponseData queryTaskList(Integer taskId) {
        SimTaskQueryBean simTaskQueryBean = new SimTaskQueryBean();
        simTaskQueryBean.setId(taskId);
        return managerService.queryTaskLogList(simTaskQueryBean);
    }
}
