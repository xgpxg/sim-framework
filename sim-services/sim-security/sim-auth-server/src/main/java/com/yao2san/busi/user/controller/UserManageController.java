package com.yao2san.busi.user.controller;

import com.yao2san.busi.user.bean.Permission;
import com.yao2san.busi.user.bean.User;
import com.yao2san.busi.user.service.UserManageService;
import com.yao2san.sim.framework.web.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("userManage")
public class UserManageController {
    @Autowired
    private UserManageService userManageService;

    @GetMapping
    public ResponseData userList(User user) {
        return userManageService.qryUserList(user);
    }

    @GetMapping("{userId}")
    public ResponseData detail(@PathVariable Long userId) {
        return userManageService.detail(userId);
    }

    @GetMapping("permissions")
    public ResponseData qryUserPermissions(Permission permission) {
        return userManageService.qryUserPermissions(permission);
    }

    @PostMapping("permissions")
    public ResponseData addOrUpdateUserPermissions(@RequestBody Map<String, Object> params) {
        return userManageService.addOrUpdateUserPermissions(params);
    }

    @GetMapping("roles")
    public ResponseData qryUserRoles(User user) {
        return userManageService.qryUserRoles(user);
    }

    @PostMapping("roles")
    public ResponseData addOrUpdateUserRoles(@RequestBody Map<String, Object> params) {
        return userManageService.addOrUpdateUserRoles(params);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public ResponseData addUser(@RequestBody @Validated(User.Add.class) User user) {
        return userManageService.addUser(user);
    }

    /**
     * 修改用户
     */
    @PatchMapping
    public ResponseData updateUser(@RequestBody @Validated(User.Update.class) User user) {
        return userManageService.updateUser(user);
    }

    //TODO 待实现

    /**
     * 上传用户头像
     */
    @PostMapping("/uploadHeadImage")
    public ResponseData uploadHeadImage(@RequestPart MultipartFile multipartFile) {
        return ResponseData.success();
    }
}
