package com.yao2san.sim.auth.client.busi.user.service.impl;


import com.yao2san.sim.auth.client.busi.user.bean.Role;
import com.yao2san.sim.auth.client.busi.user.service.RoleService;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl extends BaseServiceImpl implements RoleService {
    @Override
    public List<Role> getUserRoles(Long userId) {
        return this.sqlSession.selectList("role.getUserRoles", userId);
    }

    @Override
    public boolean hasRole(Long userId, Long roleId) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("userId", userId);
        param.put("permissionId", roleId);
        Integer count = this.sqlSession.selectOne("role.hasRole", param);
        return count > 0;
    }
}
