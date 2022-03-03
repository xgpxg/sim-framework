package com.yao2san.core;

import com.alibaba.fastjson.JSONObject;
import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.SQLParser;
import com.foundationdb.sql.parser.StatementNode;
import com.yao2san.sim.framework.utils.CacheUtil;
import com.yao2san.sim.framework.utils.CommonUtil;
import lombok.Getter;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlQuery {
    //
    private DbConfig dbConfig;

    private final static Integer MAX_ROWS = 1000;

    public SqlQuery(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public List<SqlQueryResult> selectList(String sqls) {
        if (sqls == null) {
            return null;
        }
        if (StringUtils.isEmpty(sqls.trim())) {
            return null;
        }
        sqls = sqls.trim();

        String[] sqlArr = sqls.split(";");

        List<SqlQueryResult> queryResultList = new ArrayList<>();
        for (String sql : sqlArr) {
            SqlQueryResult result = new SqlQueryResult();
            try {
                JdbcTemplate jdbcTemplate = this.getJdbcTemplate();
                StopWatch stopWatch = new StopWatch();
                if (isSelect(sql)) {
                    stopWatch.start();

                    List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
                    Long total = this.count(sql);

                    stopWatch.stop();

                    result.useTime = stopWatch.getTotalTimeMillis();
                    result.data = list;
                    result.total = total;
                } else {
                    stopWatch.start();

                    int updateRows = jdbcTemplate.update(sql);

                    stopWatch.stop();

                    result.useTime = stopWatch.getTotalTimeMillis();
                    result.total = updateRows;
                }


                result.success = true;
            } catch (Exception e) {
                result.message = "ERROR:" + e.getMessage();
                result.success = false;
            }
            queryResultList.add(result);
        }
        return queryResultList;

    }

    private boolean isSelect(String sql) {
        SQLParser parser = new SQLParser();
        StatementNode stmt = null;
        try {
            stmt = parser.parseStatement(sql);
        } catch (StandardException e) {
            e.printStackTrace();
        }
        return "SELECT".equals(stmt.statementToString());
    }

    private Long count(String sql) {
        JdbcTemplate jdbcTemplate = this.getJdbcTemplate();
        String s = "select count(0) as cnt from (" + sql + ")";
        Map<String, Object> query = jdbcTemplate.queryForMap(s);
        return MapUtils.getLong(query, "cnt");
    }

    //If update or delete, check affected rows
    private Long affectedRows(String sql) {
        //Split where condition
        return 0L;
    }

    private JdbcTemplate getJdbcTemplate() {
        String url = dbConfig.getUrl();
        String userName = dbConfig.getUsername();
        String password = dbConfig.getPassword();
        //Optional
        String driverClassName = dbConfig.getDriverClassName();
        String cacheKey = CommonUtil.md5(JSONObject.toJSONString(this.dbConfig));
        JdbcTemplate jdbcTemplate;
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
        if (jdbcTemplate != null) {
            jdbcTemplate.setMaxRows(MAX_ROWS);
        }
        return jdbcTemplate;
    }

    @Getter
    class SqlQueryResult {
        String message;
        boolean success;
        List<Map<String, Object>> data;
        long useTime;
        long total;
        long maxRows = MAX_ROWS;
    }

    public static void main(String[] args) throws StandardException {
        SQLParser sqlParser = new SQLParser();
        StatementNode statement = sqlParser.parseStatement("update a set aa=1");
        System.out.println(statement.statementToString());


        DbConfig dbConfig = new DbConfig();
        dbConfig.setUrl("jdbc:h2:file:~/database/sim-db-query;AUTO_SERVER=true;MODE=MYSQL");
        dbConfig.setUsername("sa");
        SqlQuery sqlQuery = new SqlQuery(dbConfig);
        List<SqlQueryResult> list = sqlQuery.selectList("select * from ds");
        System.out.println(JSONObject.toJSONString(list));
    }
}
