package com.yao2san.sim.auth.client.busi.user.service;


import com.yao2san.sim.auth.client.busi.user.bean.Permission;

import java.util.List;

public interface PermissionService {
    /**
     * 获取用户权限
     *
     * @param userId 用户ID
     */
    List<Permission> getUserPermissions(Long userId);

    List<Permission> getUserPermissions(Permission permission);

    /**
     * 判断用户是否具有某个权限
     *
     * @param userId       用户ID
     * @param permissionId 权限ID
     */
    boolean hasPermissions(Long userId, Long permissionId);

    /**
     * 获取用户接口权限
     *
     * @param url 访问的接口
     */
    List<Permission> getInterfacePermissions(String url);

    /**
     * 获取所有接口权限
     */
    List<Permission> getInterfacePermissions();
}
