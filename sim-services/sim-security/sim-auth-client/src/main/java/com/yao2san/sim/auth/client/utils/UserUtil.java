package com.yao2san.sim.auth.client.utils;

import com.yao2san.sim.auth.client.busi.user.bean.Permission;
import com.yao2san.sim.auth.client.busi.user.bean.Role;
import com.yao2san.sim.auth.client.busi.user.service.PermissionService;
import com.yao2san.sim.auth.client.busi.user.service.RoleService;
import com.yao2san.sim.common.entity.user.UserPrincipal;
import com.yao2san.sim.framework.utils.BeanContextUtil;
import org.apache.shiro.SecurityUtils;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserUtil {
    public static Long getCurrUserId() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityUtils.getSubject().getPrincipal();
        return userPrincipal.getUserId();
    }

    //TODO 待优化
    public static Map<String, Object> getCurrUserBaseInfo() {
        Map<String, Object> baseInfo = new HashMap<>();
        UserPrincipal userPrincipal = (UserPrincipal) SecurityUtils.getSubject().getPrincipal();
        if (userPrincipal != null) {
            baseInfo.put("username", userPrincipal.getUserName());
            //baseInfo.put("token", getCurrUserToken());

            baseInfo.put("roles", getCurrUserRoles());
            baseInfo.put("permissions", getCurrUserPermissions());
            baseInfo.put("baseInfo", userPrincipal);
        } else {
            baseInfo = getCurrUserBaseInfo("");
        }
        return baseInfo;
    }

    /**
     * 用于外部系统调用时获取用户信息
     *
     * @param accessToken access token
     */
    public static Map<String, Object> getCurrUserBaseInfo(String accessToken) {
        Map<String, Object> baseInfo = new HashMap<>();
        //TODO
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


    @SuppressWarnings("all")
    public static String getCurrUserToken() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityUtils.getSubject().getPrincipal();
        Long userId = userPrincipal.getUserId();
        String phone = userPrincipal.getPhone();

        String token = Base64.getEncoder().encodeToString((userId + phone).getBytes());
        return token;
    }
}
