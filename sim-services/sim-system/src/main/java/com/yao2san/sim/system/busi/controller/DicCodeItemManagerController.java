package com.yao2san.sim.system.busi.controller;

import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.system.busi.bean.request.DicCodeItemReq;
import com.yao2san.sim.system.busi.service.DicCodeItemManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 字典编码/字典项管理
 */
@RestController
@RequestMapping("dicCodeItem")
public class DicCodeItemManagerController {
    @Autowired
    private DicCodeItemManagerService service;
    /**
     * 查询字典编码
     */
    @GetMapping("code")
    public ResponseData qryCode(DicCodeItemReq req) {
        return service.qryCode(req);
    }

    @GetMapping("item")
    public ResponseData qryItem(DicCodeItemReq req) {
        return service.qryItem(req);
    }
}
