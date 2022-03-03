package com.yao2san.busi.user.service;


import com.github.pagehelper.PageInfo;
import com.yao2san.busi.user.bean.Permission;
import com.yao2san.busi.user.bean.request.RolePermissionReq;
import com.yao2san.sim.framework.web.response.ResponseData;

import java.util.Map;

public interface PermissionManageService {
    ResponseData qryPermissionList(Permission permission);

    ResponseData qryPermissionTree(Permission permission);

    ResponseData qryPermissionDetail(Long purviewId);

    ResponseData addOrUpdatePermission(Permission permission);

    ResponseData qryPermissionRoles(Permission permission);

    ResponseData qryPermissionUsers(Permission permission);

    ResponseData purviewForRole(Map<String,Object> params);

    ResponseData deletePermission(Long purviewId);

    ResponseData<PageInfo<Permission>> qryRolePermissions(RolePermissionReq req);
}