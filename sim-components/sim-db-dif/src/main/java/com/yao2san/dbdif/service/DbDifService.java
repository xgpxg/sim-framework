package com.yao2san.dbdif.service;

import com.yao2san.sim.framework.web.response.ResponseData;

import java.util.List;
import java.util.Map;

public interface DbDifService {
    List<Map<String, Object>> qryTables(Map<String, Object> params);

    List<Map<String, Object>> qryFields(Map<String, Object> params);

    ResponseData saveResult(Map<String, Object> params);

    ResponseData qryHistory();
}
