package com.yao2san.utils;

import com.yao2san.busi.user.bean.Permission;
import com.yao2san.busi.user.bean.Role;
import com.yao2san.busi.user.bean.User;
import com.yao2san.busi.user.service.PermissionService;
import com.yao2san.busi.user.service.RoleService;
import com.yao2san.sim.common.entity.user.UserPrincipal;
import com.yao2san.sim.framework.utils.BeanContextUtil;
import com.yao2san.sim.framework.web.exception.BusiException;
import com.yao2san.sim.framework.web.response.ResponseCode;
import org.apache.shiro.SecurityUtils;

import java.util.*;

public class UserUtil {
    public static Long getCurrUserId() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityUtils.getSubject().getPrincipal();
        return userPrincipal.getUserId();
    }

    //TODO 待优化
    public static Map<String, Object> getCurrUserBaseInfo() {
        Map<String, Object> baseInfo = new HashMap<>();
        UserPrincipal userPrincipal = (UserPrincipal) SecurityUtils.getSubject().getPrincipal();
        baseInfo.put("username", userPrincipal.getUserName());
        baseInfo.put("token", getCurrUserToken());

        baseInfo.put("roles", getCurrUserRoles());
        baseInfo.put("permissions", getCurrUserPermissions());
        baseInfo.put("baseInfo", userPrincipal);
        return baseInfo;
    }

    public static List<Role> getCurrUserRoles() {
        RoleService roleService = BeanContextUtil.getBean(RoleService.class);
        Long userId = getCurrUserId();
        return roleService.getUserRoles(userId);
    }

    public static List<Permission> getCurrUserPermissions() {
        PermissionService permissionService = BeanContextUtil.getBean(PermissionService.class);
        Long userId = getCurrUserId();
        return permissionService.getUserPermissions(userId);
    }

    public static void updateCurrUserSessionInfo(User param) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityUtils.getSubject().getPrincipal();
        if (Objects.nonNull(param.getUserName())) {
            userPrincipal.setUserName(param.getUserName());
        }
        if (Objects.nonNull(param.getEmail())) {
            userPrincipal.setEmail(param.getEmail());
        }
    }

    @SuppressWarnings("all")
    public static String getCurrUserToken() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityUtils.getSubject().getPrincipal();
        if (userPrincipal == null) {
            throw new BusiException(ResponseCode.ACCESS_RESTRICTED.getCode(), "未授权访问");
        }
        Long userId = userPrincipal.getUserId();
        String phone = userPrincipal.getPhone();

        String token = Base64.getEncoder().encodeToString((userId + phone).getBytes());
        return token;
    }
}
