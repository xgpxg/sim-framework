package com.yao2san.busi.user.controller;

import com.yao2san.busi.user.service.SecurityService;
import com.yao2san.sim.common.entity.user.UserPrincipal;
import com.yao2san.sim.framework.utils.redisUtil.RedisUtil;
import com.yao2san.sim.framework.web.response.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.yao2san.busi.constant.SystemConstant.SHIRO_AUTH_CACHE_KEY_PREFIX;

@RestController
@RequestMapping("security")
@Slf4j
public class SecurityController {
    @Autowired
    private SecurityService securityService;

    @PostMapping("login")
    public ResponseData login(@RequestBody Map<String, Object> params) {
        return securityService.login(params);
    }

    @GetMapping("logout")
    public ResponseData logout() {
        Subject subject = SecurityUtils.getSubject();
        UserPrincipal userPrincipal = (UserPrincipal) SecurityUtils.getSubject().getPrincipal();        //清除认证信息缓存
        Long id = userPrincipal.getId();
        String key = SHIRO_AUTH_CACHE_KEY_PREFIX + id;
        RedisUtil.del(key);
        subject.logout();
        return ResponseData.success();
    }

    @PostMapping("getImageCode")
    public ResponseData sendImageCode() {
        return null;
    }

    @PostMapping("sendSMSCode")
    public ResponseData sendSMSCode(String phone) {
        return null;
    }
}
