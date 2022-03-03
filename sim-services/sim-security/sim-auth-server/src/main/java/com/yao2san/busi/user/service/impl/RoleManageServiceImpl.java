package com.yao2san.busi.user.service.impl;

import com.github.pagehelper.PageInfo;
import com.yao2san.busi.user.bean.Role;
import com.yao2san.busi.user.service.RoleManageService;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import com.yao2san.utils.UserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class RoleManageServiceImpl extends BaseServiceImpl implements RoleManageService {
    @Override
    public ResponseData qryRoleList(Role role) {
        PageInfo<Map> list = this.qryList("role.qryRoleList", role);
        return ResponseData.success(list);
    }

    @Override
    @Transactional
    public ResponseData addRole(Role role) {
        if (this.isRoleCodeExist(role.getRoleCode())) {
            return ResponseData.businessError("角色编码[" + role.getRoleCode() + "]已存在");
        }
        Long userId = UserUtil.getCurrUserId();
        role.setCreateUser(userId);
        role.setUpdateUser(userId);
        role.setStatus("1000");
        this.sqlSession.insert("role.addRole", role);
        return ResponseData.success();
    }

    @Override
    @Transactional
    public ResponseData updateRole(Role role) {
        if (role.getRoleId() == null) {
            return ResponseData.businessError("角色标识不能为空");
        }
        Long userId = UserUtil.getCurrUserId();
        role.setUpdateUser(userId);
        this.sqlSession.update("role.updateRole", role);
        return ResponseData.success();
    }

    @Override
    @Transactional
    public ResponseData deleteRole(Long roleId) {
        if (roleId == null) {
            return ResponseData.businessError("角色标识不能为空");
        }
        //删除角色信息
        this.sqlSession.delete("role.deleteRole", roleId);
        //删除角色授权关系
        //TODO
        //删除角色用户关系
        //TODO
        return ResponseData.success();
    }

    private boolean isRoleCodeExist(String roleCode) {
        Integer count = this.sqlSession.selectOne("role.checkRoleCodeExist", roleCode);
        return count > 0;
    }
}
