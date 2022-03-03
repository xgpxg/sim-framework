package com.yao2san.busi.executor.controller;

import com.yao2san.sim.framework.web.response.ResponseData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("executor")
public class ExecutorController {
    @PostMapping
    public ResponseData executor(){
        return null;
    }
}
