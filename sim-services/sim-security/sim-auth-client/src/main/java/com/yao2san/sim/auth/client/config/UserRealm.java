package com.yao2san.sim.auth.client.config;

import cn.hutool.core.bean.BeanUtil;
import com.yao2san.sim.auth.client.busi.constant.SystemConstant;
import com.yao2san.sim.auth.client.busi.user.bean.Permission;
import com.yao2san.sim.auth.client.busi.user.bean.Role;
import com.yao2san.sim.auth.client.busi.user.bean.User;
import com.yao2san.sim.auth.client.busi.user.service.PermissionService;
import com.yao2san.sim.auth.client.busi.user.service.RoleService;
import com.yao2san.sim.auth.client.busi.user.service.SecurityService;
import com.yao2san.sim.common.entity.user.UserPrincipal;
import com.yao2san.sim.framework.utils.BeanContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class UserRealm extends AuthorizingRealm {
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        UserPrincipal userPrincipal = (UserPrincipal) principalCollection.getPrimaryPrincipal();
        User user = new User();
        try {
            BeanUtils.copyProperties(user, userPrincipal);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("",e);
        }

        RoleService roleService = BeanContextUtil.getBean(RoleService.class);
        PermissionService permissionService = BeanContextUtil.getBean(PermissionService.class);

        //用户角色
        List<Role> userRoles = roleService.getUserRoles(user.getUserId());
        //用户权限
        List<Permission> userPermissions = permissionService.getUserPermissions(user.getUserId());

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        if (userRoles != null) {
            for (Role r : userRoles) {
                simpleAuthorizationInfo.addRole(r.getRoleName());
            }
        }
        if (userPermissions != null) {
            for (Permission permission : userPermissions) {
                if (StringUtils.isNotEmpty(permission.getPurviewCode())) {
                    simpleAuthorizationInfo.addStringPermission(permission.getPurviewCode());
                }
            }
        }

        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String userName = (String) authenticationToken.getPrincipal();
        String userPwd = new String((char[]) authenticationToken.getCredentials());
        UserPrincipal userPrincipal = this.getUserBaseInfoByUsername(userName);
        userPrincipal.setId(userPrincipal.getUserId());
        String password = userPrincipal.getPassword();
        if (userName == null) {
            throw new AccountException("用户名不正确");
        } else if (!userPwd.equals(password)) {
            throw new AccountException("密码不正确");
        }
        userPrincipal.setPassword(null);
        return new SimpleAuthenticationInfo(userPrincipal, password, getName());
    }


    private Map<String, Object> getUserBaseInfoByPhone(String phone) {
        SqlSession sqlSession = BeanContextUtil.getBean(SqlSession.class);
        return sqlSession.selectOne("user.qryUserBaseInfoByPhone", phone);

    }

    private UserPrincipal getUserBaseInfoByUsername(String userName) {
        SqlSession sqlSession = BeanContextUtil.getBean(SqlSession.class);
        Map<String, Object> o = sqlSession.selectOne("user.qryUserBaseInfoByUsername", userName);
        return BeanUtil.fillBeanWithMap(o, new UserPrincipal(), false);

    }

    private Map<String, Object> getUserBaseInfoByUserId(Long userId) {
        SqlSession sqlSession = BeanContextUtil.getBean(SqlSession.class);
        return sqlSession.selectOne("user.qryUserBaseInfoByUserId", userId);

    }

    private boolean checkAuthExists(String openid, SystemConstant.LoginType loginType) {
        SecurityService securityService = BeanContextUtil.getBean(SecurityService.class);
        return securityService.checkAuthExists(openid, loginType);
    }

}
