package com.yao2san.dbdif.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yao2san.dbdif.service.DbDifService;
import com.yao2san.dbdif.utils.CacheUtil;
import com.yao2san.dbdif.utils.CommonUtil;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DbDifServiceImpl extends BaseServiceImpl implements DbDifService {


    @Override
    @SuppressWarnings("all")
    public List<Map<String, Object>> qryTables(Map<String, Object> params) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(params);
        String type = MapUtils.getString(params, "type");
        String sql = getQryTablesSql(type);
        List<Map<String, Object>> list = null;
        if ("mysql".equals(type)) {
            list = jdbcTemplate.queryForList(sql, params.get("database") + "%");
        } else if ("oracle".equals(type)) {
            list = jdbcTemplate.queryForList(sql);
        }
        return list;
    }

    private String getQryTablesSql(String dbType) {
        String sql = "";
        switch (dbType) {
            case "mysql":
                sql = "SELECT * FROM INFORMATION_SCHEMA.`TABLES` WHERE TABLE_SCHEMA like ? ORDER BY TABLE_NAME ASC, create_time DESC";
                break;
            case "oracle":
                sql = "select * from user_tab_comments where TABLE_NAME not like '%$%' ORDER BY TABLE_NAME";
                break;
        }
        return sql;
    }

    @Override
    public List<Map<String, Object>> qryFields(Map<String, Object> params) {
        Map dbInfo = MapUtils.getMap(params, "dbInfo");
        String tableName = MapUtils.getString(params, "tableName");
        JdbcTemplate jdbcTemplate = getJdbcTemplate((Map<String, Object>) dbInfo);
        String type = MapUtils.getString(dbInfo, "type");
        String sql = getQryFieldsSql(type, tableName);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }

    private String getQryFieldsSql(String dbType, String tableName) {
        String sql = "";
        switch (dbType) {
            case "mysql":
                sql = "show full columns from " + tableName;
                break;
            case "oracle":
                sql = "select COLUMN_NAME as \"Field\",DATA_TYPE||'('||CHAR_COL_DECL_LENGTH||')' as \"Type\",NULLABLE as \"Null\",DATA_DEFAULT as \"Default\" from user_tab_columns where TABLE_NAME ='" + tableName + "'";
                break;
        }
        return sql;
    }

    @Override
    public ResponseData saveResult(Map<String, Object> params) {
        sqlSession.insert("dbDifMapper.insertHistory", params);
        return ResponseData.success();
    }

    @Override
    public ResponseData qryHistory() {
        List<Map<String, String>> list = sqlSession.selectList("dbDifMapper.qryHistory");
        List<Map<String, Object>> result = new ArrayList<>();
        list.forEach(li -> {
            Map<String, Object> map = new HashMap<>();
            map.put("db1", JSONObject.parseObject(li.get("db1")));
            map.put("db2", JSONObject.parseObject(li.get("db2")));
            map.put("dfJsonOriginal", JSONObject.parseArray(li.get("dfJsonOriginal")));
            map.put("dfJson", JSONObject.parseArray(li.get("dfJson")));
            map.put("createDate", li.get("createDate"));
            result.add(map);
        });
        return ResponseData.success(result);
    }

    private JdbcTemplate getJdbcTemplate(Map<String, Object> params) {
        String type = MapUtils.getString(params, "type");
        String driverClassName = "";
        switch (type) {
            case "mysql":
                driverClassName = "com.mysql.jdbc.Driver";
                break;
            case "oracle":
                driverClassName = "oracle.jdbc.driver.OracleDriver";
                break;
        }
        String ip = MapUtils.getString(params, "ip");
        String port = MapUtils.getString(params, "port");
        String userName = MapUtils.getString(params, "username");
        String password = MapUtils.getString(params, "password");
        String dbName = MapUtils.getString(params, "database");
        String param = MapUtils.getString(params, "param");
        String url = "";
        switch (type) {
            case "mysql":
                url = "jdbc:mysql://" + ip + ":" + port + "/" + dbName + "?" + param;
                break;
            case "oracle":
                url = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + dbName + (StringUtils.isEmpty(param) ? "" : ("?" + param));
                break;
        }
        String cacheKey = CommonUtil.md5(JSONObject.toJSONString(params));
        JdbcTemplate jdbcTemplate = null;
        if (CacheUtil.get(cacheKey) == null) {
            DataSource dataSource = DataSourceBuilder.create()
                    .driverClassName(driverClassName)
                    .url(url)
                    .username(userName)
                    .password(password)
                    .build();
            jdbcTemplate = new JdbcTemplate(dataSource);
            CacheUtil.put(cacheKey, jdbcTemplate, 1000 * 60 * 30);
        } else {
            jdbcTemplate = CacheUtil.get(cacheKey);
        }
        return jdbcTemplate;
    }

}
