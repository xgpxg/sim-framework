package com.yao2san.sim.framework.web.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yao2san.sim.framework.web.bean.Pagination;
import com.yao2san.sim.framework.web.service.BaseService;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("baseService")
public class BaseServiceImpl implements BaseService {
    @Autowired(required = false)
    public SqlSessionTemplate sqlSession;

    @Override
    public List<Map<String, Object>> getDicList(String code) {
        return sqlSession.selectList("baseMapper.getDicList", code);
    }

    @Override
    public Map<String, Object> getDicItem(String dicCode, String itemCode) {
        Map<String, String> param = new HashMap<>(2);
        param.put("dicCode", dicCode);
        param.put("itemCode", itemCode);
        return sqlSession.selectOne("baseMapper.getDicItem", param);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getDicItemValue(String dicCode, String itemCode) {
        Map<String, Object> dicItem = this.getDicItem(dicCode, itemCode);
        if (dicItem == null) {
            return null;
        }
        return (String) dicItem.get("itemValue");
    }

    @Override
    public String getSignalDicCodeValue(String dicCode) {
        return this.getSignalDicCodeValue(dicCode, null);
    }

    @Override
    public String getSignalDicCodeValue(String dicCode, String defaultValue) {
        String value = this.sqlSession.selectOne("baseMapper.getSignalDicCodeValue", dicCode);
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }


    @Override
    public <T> PageInfo<T> qryList(String mapper, Pagination page) {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<T> list = sqlSession.selectList(mapper, page);
        return new PageInfo<>(list);
    }

    @Override
    public int nativeUpdate(String sql) {
        Map<String, String> param = Collections.singletonMap("sql", sql);
        return sqlSession.update("baseMapper.nativeUpdate", param);
    }

    @Override
    public <T> List<T> nativeSelect(String sql) {
        Map<String, String> param = Collections.singletonMap("sql", sql);
        return sqlSession.selectList("baseMapper.nativeSelect", param);
    }

    @Override
    public int nativeDelete(String sql) {
        Map<String, String> param = Collections.singletonMap("sql", sql);
        return sqlSession.delete("baseMapper.nativeDelete", param);
    }
}
