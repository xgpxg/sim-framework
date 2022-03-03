package com.yao2san.sim.framework.web.service;

import com.github.pagehelper.PageInfo;
import com.yao2san.sim.framework.web.bean.Pagination;

import java.util.List;
import java.util.Map;

public interface BaseService {
    List<Map<String, Object>> getDicList(String code);

    Map<String, Object> getDicItem(String dicCode, String itemCode);

    String getDicItemValue(String dicCode, String itemCode);

    String getSignalDicCodeValue(String dicCode);

    String getSignalDicCodeValue(String dicCode,String defaultValue);

    <T> PageInfo<T> qryList(String mapper, Pagination page);

    int nativeUpdate(String sql);

    <T> List<T> nativeSelect(String sql);

    int nativeDelete(String sql);
}
