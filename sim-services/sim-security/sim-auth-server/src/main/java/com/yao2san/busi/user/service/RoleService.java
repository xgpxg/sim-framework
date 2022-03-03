package com.yao2san.busi.user.service;

import com.yao2san.busi.user.bean.Role;

import java.util.List;

public interface RoleService {
    /**
     * 获取用户角色
     *
     * @param userId 用户ID
     */
    List<Role> getUserRoles(Long userId);

    /**
     * 判断用户是否具有某个权限
     *
     * @param userId 用户ID
     * @param roleId 权限ID
     */
    boolean hasRole(Long userId, Long roleId);
}
