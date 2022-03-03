package com.yao2san.busi.user.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.PageInfo;
import com.yao2san.busi.user.bean.Permission;
import com.yao2san.busi.user.bean.request.RolePermissionReq;
import com.yao2san.busi.user.service.PermissionManageService;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import com.yao2san.utils.UserUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class PermissionManageServiceImpl extends BaseServiceImpl implements PermissionManageService {
    @Override
    public ResponseData qryPermissionList(Permission permission) {
        PageInfo<Permission> list = this.qryList("permissionManage.qryPermissionList", permission);
        return ResponseData.success(list);
    }

    @Override
    public ResponseData qryPermissionTree(Permission permission) {
        List<Permission> list = this.sqlSession.selectList("permissionManage.qryPermissionList", permission);
        List<Map<String, Object>> tree = this.buildTree(list);
        return ResponseData.success(tree);
    }

    @Override
    public ResponseData qryPermissionDetail(Long purviewId) {
        Permission data = this.sqlSession.selectOne("permissionManage.qryPermissionDetail", purviewId);
        return ResponseData.success(data);
    }

    @Override
    public ResponseData addOrUpdatePermission(Permission permission) {
        permission.setCreateUser(UserUtil.getCurrUserId());
        if (permission.getPurviewId() == null) {
            if (this.checkCodeExist(permission)) {
                return ResponseData.businessError("权限编码" + permission.getPurviewCode() + "已存在");
            }
            permission.setStatus("1000");
            this.sqlSession.insert("permissionManage.addPermission", permission);

            permission.setPath(this.getPath(permission.getPurviewId()));
            this.sqlSession.update("permissionManage.updatePermission", permission);

        } else {
            permission.setUpdateUser(UserUtil.getCurrUserId());
            permission.setPath(this.getPath(permission.getPurviewId()));
            this.sqlSession.update("permissionManage.updatePermission", permission);
        }
        return ResponseData.success(permission);
    }

    private String getPath(Long id) {
        Permission curr = this.sqlSession.selectOne("permissionManage.qryPermissionDetail", id);
        if (curr.getParentId() == null || curr.getParentId() <= 0) {
            return "/" + id;
        } else {
            Permission parent = this.sqlSession.selectOne("permissionManage.qryPermissionDetail", curr.getParentId());
            return parent.getPath() + "/" + id;
        }
    }

    private boolean checkCodeExist(Permission permission) {
        Integer count = this.sqlSession.selectOne("permissionManage.countPermissionByCode", permission);
        return count > 0;
    }

    private List<Map<String, Object>> buildTree(List<Permission> permissions) {
        Map<String, Map<String, Object>> map = new HashMap<>();
        List<Map<String, Object>> tree = new ArrayList<>();
        permissions.forEach(permission -> {
            Map<String, Object> m = JSONObject.parseObject(JSONObject.toJSONString(permission), new TypeReference<Map<String, Object>>() {
            });
            m.put("children", new ArrayList<>());
            map.put(String.valueOf(permission.getPurviewId()), m);
        });
        permissions.forEach(permission -> {
            Map<String, Object> m = map.get(String.valueOf(permission.getPurviewId()));
            if (permission.getParentId() != null && permission.getParentId() > 0) {
                String pid = String.valueOf(permission.getParentId());
                Map<String, Object> pm = map.get(pid);
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) pm.get("children");
                children.add(m);
            } else {
                tree.add(m);
            }
        });
        return tree;
    }

    public ResponseData qryPermissionRoles(Permission permission) {
        PageInfo<Object> list;
        if ("1".equals(permission.getIsPermissions())) {
            list = this.qryList("permissionManage.qryPermissionRoles", permission);
        } else if ("0".equals(permission.getIsPermissions())) {
            list = this.qryList("permissionManage.qryNotPermissionRoles", permission);
        } else {
            list = this.qryList("permissionManage.qryAllPermissionRoles", permission);
        }
        return ResponseData.success(list);
    }

    @Override
    public ResponseData qryPermissionUsers(Permission permission) {
        PageInfo<Object> list;
        if ("1".equals(permission.getIsPermissions())) {
            list = this.qryList("permissionManage.qryPermissionUsers", permission);
        } else if ("0".equals(permission.getIsPermissions())) {
            list = this.qryList("permissionManage.qryNotPermissionUsers", permission);
        } else {
            list = this.qryList("permissionManage.qryAllPermissionUsers", permission);
        }
        return ResponseData.success(list);
    }

    @Override
    public ResponseData purviewForRole(Map<String, Object> params) {
        JSONObject object = JSONObject.parseObject(JSONObject.toJSONString(params));
        JSONArray roles = object.getJSONArray("roles");
        JSONArray permissions = object.getJSONArray("permissions");
        //操作类型: 1 增加授权 2 取消授权
        String option = object.getString("option");
        if (roles == null
                || permissions == null
                || roles.size() == 0
                || permissions.size() == 0
                || roles.get(0) == null
                || permissions.get(0) == null) {
            return ResponseData.businessError("权限和角色不能为空");
        }
        List<Map<String, Long>> list = new ArrayList<>();
        Long currUserId = UserUtil.getCurrUserId();
        for (Object role : roles) {
            Long roleId = Long.valueOf(String.valueOf(role));
            for (Object permission : permissions) {
                Long purviewId = Long.valueOf(String.valueOf(permission));
                Map<String, Long> map = new HashMap<>(2);
                map.put("roleId", roleId);
                map.put("purviewId", purviewId);
                map.put("createUser", currUserId);
                list.add(map);
            }
        }
        log.info("权限授权角色变更(option={}):{}", option, list);
        //增加授权
        if ("1".equals(option)) {
            //先取消授权
            this.sqlSession.delete("permissionManage.delPurviewRoleRel", list);
            //重新授权
            this.sqlSession.insert("permissionManage.addPurviewRoleRel", list);
        } else if ("2".equals(option)) {
            //取消授权
            this.sqlSession.delete("permissionManage.delPurviewRoleRel", list);
        } else {
            return ResponseData.businessError("不支持的操作类型: option=" + option);
        }

        return ResponseData.success(null, "操作成功");
    }

    @Transactional
    @Override
    public ResponseData deletePermission(Long purviewId) {
        this.delPermission(purviewId);
        return ResponseData.success();
    }

    @Override
    public ResponseData<PageInfo<Permission>> qryRolePermissions(RolePermissionReq req) {
        PageInfo<Permission> pageInfo;
        if ("1".equals(req.getIsPermissions())) {
            pageInfo = this.qryList("permissionManage.qryRolePermissions", req);
        } else if ("0".equals(req.getIsPermissions())) {
            pageInfo = this.qryList("permissionManage.qryRoleNotPermissions", req);
        }else{
            pageInfo = this.qryList("permissionManage.qryRoleAllPermissions",req);
        }

        return ResponseData.success(pageInfo);
    }

    private void delPermission(Long purviewId) {
        Permission permission = this.sqlSession.selectOne("permissionManage.qryPermissionDetail", purviewId);
        if (permission != null) {
            //子节点
            List<Permission> childPermissions = this.sqlSession.selectList(
                    "permissionManage.qryPermissionList",
                    new Permission().setParentId(permission.getPurviewId()));
            if (childPermissions.size() > 0) {
                for (Permission childPermission : childPermissions) {
                    this.delPermission(childPermission.getPurviewId());
                }
            }
            log.debug("remove permission:{}", permission.getPurviewId());
            this.sqlSession.delete("permissionManage.deletePermission", permission.getPurviewId());
        }
    }
}
