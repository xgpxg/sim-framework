package com.yao2san.sim.gateway.config.mybatis;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;

@MappedTypes(Set.class)
@MappedJdbcTypes(value = {JdbcType.VARCHAR},includeNullJdbcType = true)
public class SetTypeHandler extends BaseTypeHandler<Set<String>> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Set<String> strings, JdbcType jdbcType) throws SQLException {
        if (strings == null || strings.isEmpty()) {
            preparedStatement.setString(i, null);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(s).append(",");
        }
        preparedStatement.setString(i, sb.toString().substring(0, sb.toString().length() - 1));
    }

    @Override
    public Set<String> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        if (StringUtils.isEmpty(resultSet.getString(s))) {
            return null;
        }
        String[] arr = resultSet.getString(s).split(",");
        return Sets.newLinkedHashSet(Arrays.asList(arr));
    }

    @Override
    public Set<String> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        if (StringUtils.isEmpty(resultSet.getString(i))) {
            return null;
        }
        String[] arr = resultSet.getString(i).split(",");
        return Sets.newLinkedHashSet(Arrays.asList(arr));
    }

    @Override
    public Set<String> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        if (StringUtils.isEmpty(callableStatement.getString(i))) {
            return null;
        }
        String[] arr = callableStatement.getString(i).split(",");
        return Sets.newLinkedHashSet(Arrays.asList(arr));
    }
}
