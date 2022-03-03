package com.yao2san.busi.user.service.impl;

import com.yao2san.busi.user.bean.Permission;
import com.yao2san.busi.user.service.PermissionService;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PermissionServiceImpl extends BaseServiceImpl implements PermissionService {

    @Override
    public List<Permission> getUserPermissions(Long userId) {
        Permission permission = new Permission();
        permission.setUserId(userId);
        return this.getUserPermissions(permission);
    }

    @Override
    public List<Permission> getUserPermissions(Permission permission) {
        return this.sqlSession.selectList("permission.getUserPermissions", permission);
    }

    @Override
    public boolean hasPermissions(Long userId, Long permissionId) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("userId", userId);
        param.put("permissionId", permissionId);
        Integer count = this.sqlSession.selectOne("permission.hasPermission", param);
        return count > 0;
    }

    @Override
    public List<Permission> getInterfacePermissions(String url) {
        return this.sqlSession.selectList("permission.getInterfacePermissions", url);
    }

    @Override
    public List<Permission> getInterfacePermissions() {
        return this.sqlSession.selectList("permission.getInterfacePermissions");
    }
}
