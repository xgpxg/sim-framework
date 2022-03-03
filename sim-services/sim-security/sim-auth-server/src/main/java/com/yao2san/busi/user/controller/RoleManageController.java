package com.yao2san.busi.user.controller;

import com.yao2san.busi.user.bean.Role;
import com.yao2san.busi.user.bean.User;
import com.yao2san.busi.user.service.RoleManageService;
import com.yao2san.busi.user.service.UserManageService;
import com.yao2san.sim.framework.web.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("role")
public class RoleManageController {
    @Autowired
    private RoleManageService roleManageService;

    /**
     * 查询权限列表
     */
    @GetMapping
    public ResponseData roleList(Role role) {
        return roleManageService.qryRoleList(role);
    }

    @PostMapping
    public ResponseData addRole(@RequestBody Role role){
        return roleManageService.addRole(role);
    }
    @PatchMapping
    public ResponseData updateRole(@RequestBody Role role){
        return roleManageService.updateRole(role);
    }
    @DeleteMapping("{roleId}")
    public ResponseData deleteRole(@PathVariable Long roleId){
        return roleManageService.deleteRole(roleId);
    }
}
