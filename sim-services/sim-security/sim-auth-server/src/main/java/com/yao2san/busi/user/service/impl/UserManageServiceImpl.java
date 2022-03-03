package com.yao2san.busi.user.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yao2san.busi.user.bean.Permission;
import com.yao2san.busi.user.bean.Role;
import com.yao2san.busi.user.bean.User;
import com.yao2san.busi.user.service.UserManageService;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import com.yao2san.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserManageServiceImpl extends BaseServiceImpl implements UserManageService {
    @Override
    public ResponseData qryUserList(User user) {
        //用户信息
        PageInfo<Map<String, Object>> list = this.qryList("userManage.qryUserList", user);
        //角色
        list.getList().forEach(u -> {
            List<Role> roles = this.sqlSession.selectList("role.getUserRoles", MapUtils.getLong(u, "userId"));
            u.put("roles", roles.stream().map(Role::getRoleName).collect(Collectors.joining(",")));
        });
        return ResponseData.success(list);
    }

    @Override
    public ResponseData qryUserPermissions(Permission permission) {
        PageInfo<Object> list;
        if ("1".equals(permission.getIsPermissions())) {
            list = this.qryList("userManage.qryUserPermissions", permission);
        } else {
            list = this.qryList("userManage.qryUserNotPermissions", permission);
        }
        return ResponseData.success(list);
    }

    @Override
    @Transactional
    public ResponseData addOrUpdateUserPermissions(Map<String, Object> params) {
        JSONObject object = JSONObject.parseObject(JSONObject.toJSONString(params));
        JSONArray userIds = object.getJSONArray("userIds");
        JSONArray permissions = object.getJSONArray("permissions");
        //操作类型: 1 增加授权 2 取消授权
        String option = object.getString("option");
        if (userIds == null
                || permissions == null
                || userIds.size() == 0
                || permissions.size() == 0
                || userIds.get(0) == null
                || permissions.get(0) == null) {
            return ResponseData.businessError("权限和用户标识不能为空");
        }
        List<Map<String, Long>> list = new ArrayList<>();
        Long currUserId = UserUtil.getCurrUserId();
        for (Object userId : userIds) {
            Long uid = Long.valueOf(String.valueOf(userId));
            for (Object permission : permissions) {
                Long purviewId = Long.valueOf(String.valueOf(permission));
                Map<String, Long> map = new HashMap<>(2);
                map.put("userId", uid);
                map.put("purviewId", purviewId);
                map.put("createUser", currUserId);
                list.add(map);
            }
        }
        log.info("用户特殊授权变更(option={}):{}", option, list);
        //增加授权
        if ("1".equals(option)) {
            //先取消授权
            this.sqlSession.delete("userManage.delPurviewUserRel", list);
            //重新授权
            this.sqlSession.insert("userManage.addPurviewUserRel", list);
        } else if ("2".equals(option)) {
            //取消授权
            this.sqlSession.delete("userManage.delPurviewUserRel", list);
        } else {
            return ResponseData.businessError("不支持的操作类型: option=" + option);
        }

        return ResponseData.success(null, "操作成功");
    }

    @Override
    public ResponseData qryUserRoles(User user) {
        PageInfo<Map> list = this.qryList("userManage.qryUserRoles", user);
        return ResponseData.success(list);
    }

    @Override
    @Transactional
    public ResponseData addOrUpdateUserRoles(Map<String, Object> params) {
        JSONObject object = JSONObject.parseObject(JSONObject.toJSONString(params));
        JSONArray userIds = object.getJSONArray("userIds");
        JSONArray permissions = object.getJSONArray("roleIds");
        //操作类型: 1 增加授权 2 取消授权
        String option = object.getString("option");
        if (userIds == null
                || permissions == null
                || userIds.size() == 0
                || permissions.size() == 0
                || userIds.get(0) == null
                || permissions.get(0) == null) {
            return ResponseData.businessError("角色和用户标识不能为空");
        }
        List<Map<String, Long>> list = new ArrayList<>();
        Long currUserId = UserUtil.getCurrUserId();
        for (Object userId : userIds) {
            Long uid = Long.valueOf(String.valueOf(userId));
            for (Object role : permissions) {
                Long roleId = Long.valueOf(String.valueOf(role));
                Map<String, Long> map = new HashMap<>(2);
                map.put("userId", uid);
                map.put("roleId", roleId);
                map.put("createUser", currUserId);
                list.add(map);
            }
        }
        log.info("用户角色变更(option={}):{}", option, list);
        //增加授权
        if ("1".equals(option)) {
            //先取消授权
            this.sqlSession.delete("userManage.delUserRoleRel", list);
            //重新授权
            this.sqlSession.insert("userManage.addUserRoleRel", list);
        } else if ("2".equals(option)) {
            //取消授权
            this.sqlSession.delete("userManage.delUserRoleRel", list);
        } else {
            return ResponseData.businessError("不支持的操作类型: option=" + option);
        }

        return ResponseData.success(null, "操作成功");
    }

    @Override
    @Transactional
    public ResponseData addUser(User user) {
        user.setPassword(user.getPhone().substring(5, 10));
        this.sqlSession.insert("userManage.addUser", user);
        return ResponseData.success();
    }

    @Override
    @Transactional
    public ResponseData updateUser(User user) {
        this.sqlSession.update("userManage.updateUser", user);
        return ResponseData.success();
    }

    @Override
    public ResponseData detail(Long userId) {
        Map<String, Object> data = this.sqlSession.selectOne("userManage.qryUser", userId);
        //TODO 查询用户属性
        return ResponseData.success(data);
    }
}
