package com.yao2san.busi.user.service;

import com.yao2san.busi.user.bean.Permission;
import com.yao2san.busi.user.bean.Role;
import com.yao2san.busi.user.bean.User;
import com.yao2san.sim.framework.web.response.ResponseData;

import java.util.Map;

public interface UserManageService {
    /**
     * 查询用户列表
     */
    ResponseData qryUserList(User user);

    ResponseData qryUserPermissions(Permission permission);

    ResponseData addOrUpdateUserPermissions(Map<String, Object> params);

    ResponseData qryUserRoles(User user);

    ResponseData addOrUpdateUserRoles(Map<String, Object> params);

    ResponseData addUser(User user);

    ResponseData updateUser(User user);

    ResponseData detail(Long userId);
}
