package com.yao2san.busi.user.controller;

import com.github.pagehelper.PageInfo;
import com.yao2san.busi.user.bean.Permission;
import com.yao2san.busi.user.bean.Role;
import com.yao2san.busi.user.bean.request.RolePermissionReq;
import com.yao2san.busi.user.service.PermissionManageService;
import com.yao2san.sim.framework.web.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("permission")
public class PermissionManageController {
    @Autowired
    private PermissionManageService permissionManageService;

    /**
     * 查询权限列表
     */
    @GetMapping
    public ResponseData qryPermissionList(Permission permission) {
        return permissionManageService.qryPermissionList(permission);
    }

    /**
     * 查询权限树
     */
    @GetMapping("tree")
    public ResponseData qryPermissionTree(Permission permission) {
        return permissionManageService.qryPermissionTree(permission);
    }

    /**
     * 查询权限详情
     */
    @GetMapping("{purviewId}")
    public ResponseData qryPermissionDetail(@PathVariable("purviewId") Long purviewId) {
        return permissionManageService.qryPermissionDetail(purviewId);
    }

    @PostMapping
    public ResponseData addPermission(@Validated(Permission.ADD.class) @RequestBody Permission permission) {
        return permissionManageService.addOrUpdatePermission(permission);
    }

    /**
     * 查询指定权限已授权/未授权的角色
     */
    @GetMapping("roles")
    public ResponseData qryPermissionRoles(Permission permission) {
        return permissionManageService.qryPermissionRoles(permission);
    }
    @GetMapping("users")
    public ResponseData qryPermissionUsers(Permission permission) {
        return permissionManageService.qryPermissionUsers(permission);
    }
    /**
     * 为角色增加授权
     *
     * @param params 需传递两个对象：角色ID数组和权限ID数组
     */
    @PostMapping("purview")
    public ResponseData purviewForRole(@RequestBody Map<String, Object> params) {
        return permissionManageService.purviewForRole(params);
    }

    @DeleteMapping("{purviewId}")
    public ResponseData deletePermission(@PathVariable Long purviewId) {
        return permissionManageService.deletePermission(purviewId);
    }

    /**
     * 查询指定角色已授权/未授权的权限
     */
    @GetMapping("rolePermission")
    public ResponseData<PageInfo<Permission>> qryRolePermissions(RolePermissionReq req) {
        return permissionManageService.qryRolePermissions(req);
    }
}
