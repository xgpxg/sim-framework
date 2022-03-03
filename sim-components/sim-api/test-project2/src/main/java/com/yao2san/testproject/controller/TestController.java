package com.yao2san.testproject.controller;

import com.yao2san.simapiclient.config.SimApiServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("test")
public class TestController {
    @Value("${spring.application.name}")
    private String appName;
    @Autowired
    private SimApiServerConfig simApiServerConfig;
    @Autowired
    private WebApplicationContext webApplicationContext  ;
    @RequestMapping(value = "/test",method = {RequestMethod.GET,RequestMethod.DELETE})
    public Object test(){
        return simApiServerConfig;
    }

    @GetMapping
    public Object test2(){
        return webApplicationContext.getBeansWithAnnotation(RequestMapping.class);
    }
    @PostMapping
    public Object test3(@RequestParam(required = false) String s){
        return null;
    }
    private static List<String> urls = new ArrayList<String>();

}
