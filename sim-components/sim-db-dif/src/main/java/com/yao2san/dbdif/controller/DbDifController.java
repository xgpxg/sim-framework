package com.yao2san.dbdif.controller;

import com.yao2san.dbdif.service.DbDifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import com.yao2san.sim.framework.web.response.ResponseData;

import java.util.List;
import java.util.Map;

@Controller
public class DbDifController {
    @Autowired
    private DbDifService dbDifService;

    @GetMapping({"/", "", "home"})
    public String home() {
        return "home";
    }

    @GetMapping("history")
    public String history() {
        return "history";
    }

    @GetMapping("setting")
    public String setting() {
        return "setting";
    }

    @PostMapping("qryTables")
    @ResponseBody
    public List<Map<String, Object>> qryTables(@RequestBody Map<String, Object> params) {
        return dbDifService.qryTables(params);
    }

    @PostMapping("qryFields")
    @ResponseBody
    public List<Map<String, Object>> qryFields(@RequestBody Map<String, Object> params) {
        return dbDifService.qryFields(params);
    }

    @PostMapping("saveResult")
    @ResponseBody
    public ResponseData saveResult(@RequestBody Map<String, Object> params) {
        return dbDifService.saveResult(params);
    }

    @GetMapping("qryHistory")
    @ResponseBody
    public ResponseData qryHistory() {
        return dbDifService.qryHistory();
    }
}
