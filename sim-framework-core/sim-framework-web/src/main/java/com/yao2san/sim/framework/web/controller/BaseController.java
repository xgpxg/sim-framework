package com.yao2san.sim.framework.web.controller;


import com.yao2san.sim.framework.web.exception.BusiException;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.framework.web.service.BaseService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/common")
public class BaseController {
    private final BaseService baseService;

    private final static String FIELD_TYPE = "fieldType";

    public BaseController(@Qualifier("baseService") BaseService baseService) {
        this.baseService = baseService;
    }

    @GetMapping("/dic")
    public ResponseData getDicList(String code, @RequestParam(required = false) Boolean showAll) {
        if (FIELD_TYPE.equals(code)) {
            throw new BusiException("发生了异常");
        }
        List<Map<String, Object>> dicList = baseService.getDicList(code);
        if (showAll) {
            Map<String, Object> all = new HashMap<>(2);
            all.put("itemCode", "");
            all.put("itemText", "全部");
            dicList.add(0,all);
        }
        return ResponseData.success(dicList);
    }
}
