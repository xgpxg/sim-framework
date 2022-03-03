package com.yao2san.busi.user.service;

import com.yao2san.busi.user.bean.Role;
import com.yao2san.sim.framework.web.response.ResponseData;

public interface RoleManageService {
    /**
     * 查询用户列表
     */
    ResponseData qryRoleList(Role role);

    /**
     * 新增角色
     */
    ResponseData addRole(Role role);

    /**
     * 更新角色
     */
    ResponseData updateRole(Role role);

    /**
     * 删除角色
     */
    ResponseData deleteRole(Long roleId);
}
