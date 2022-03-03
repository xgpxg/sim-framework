package com.yao2san.sim.gateway.monitor.reporter;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReporterHelper {
    private static SqlSession sqlSession;

    @Autowired
    public void setSqlSession(SqlSession sqlSession) {
        ReporterHelper.sqlSession = sqlSession;
    }

    public static void addReport(DatabaseReport report) {
        sqlSession.insert("monitor.add", report);
    }
}
