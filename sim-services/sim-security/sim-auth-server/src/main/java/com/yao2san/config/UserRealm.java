package com.yao2san.config;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.yao2san.busi.constant.SystemConstant;
import com.yao2san.busi.user.bean.Permission;
import com.yao2san.busi.user.bean.Role;
import com.yao2san.busi.user.bean.User;
import com.yao2san.busi.user.bean.UserAuth;
import com.yao2san.busi.user.service.PermissionService;
import com.yao2san.busi.user.service.RoleService;
import com.yao2san.busi.user.service.SecurityService;
import com.yao2san.sim.common.entity.user.UserPrincipal;
import com.yao2san.sim.framework.utils.BeanContextUtil;
import com.yao2san.sim.framework.utils.CommonUtil;
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
            log.error("", e);
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
        log.debug(JSONObject.toJSONString(userPermissions));
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String userName = (String) authenticationToken.getPrincipal();
        String userPwd = new String((char[]) authenticationToken.getCredentials());
        if (userPwd.startsWith("vCode_")) {
            //手机号登陆
            /*final String phone = userName;
            final String vCode = userPwd.replace("vCode_", "").trim();
            boolean smsCheck = SmsUtil.smsCheck(phone, vCode);
            if (smsCheck) {
                Map<String, Object> userBaseInfo = this.getUserBaseInfoByPhone(phone);
                if (userBaseInfo == null) {
                    throw new UnknownAccountException("手机号未注册");
                }
                userBaseInfo.remove("password");
                return new SimpleAuthenticationInfo(userBaseInfo, userPwd, getName());
            } else {
                throw new AccountException("验证码不正确或已过期");
            }*/

            return null;
        } else if (userPwd.startsWith("openid_")) {
            //微信小程序登陆
            String openid = userName;
            boolean exists = checkAuthExists(openid, SystemConstant.LoginType.XCX);
            SecurityService securityService = BeanContextUtil.getBean(SecurityService.class);
            if (exists) {
                // user_auth表存在openid
                // 查询旧的授权信息
                UserAuth userAuthOld = securityService.qryUserAuth(openid, SystemConstant.LoginType.XCX);
                // 更新授权
                UserAuth userAuth = new UserAuth();
                userAuth.setUserId(userAuthOld.getUserId());
                userAuth.setLoginType(SystemConstant.LoginType.XCX.getTypeName());
                securityService.updateUserAuth(userAuth);
                // 然后调用登陆流程
                Map<String, Object> userBaseInfo = this.getUserBaseInfoByUserId(userAuth.getUserId());
                if (userBaseInfo == null) {
                    throw new UnknownAccountException("用户不存在");
                }
                return new SimpleAuthenticationInfo(userBaseInfo, userPwd, getName());
            } else {
                // user_auth表不存在openid
                // 新增用户信息
                User user = new User();
                user.setUserName("wx_" + CommonUtil.getRandomString(8));
                SqlSession sqlSession = BeanContextUtil.getBean(SqlSession.class);
                sqlSession.insert("user.addUser", user);
                // 新增授权信息 关联用户ID
                UserAuth userAuth = new UserAuth();
                userAuth.setUserId(user.getUserId());
                userAuth.setLoginType(SystemConstant.LoginType.XCX.getTypeName());
                userAuth.setOpenid(openid);
                userAuth.setStatus("1000");
                userAuth.setUserType("1000");
                securityService.addUserAuth(userAuth);
                // 然后调用登陆流程
                Map<String, Object> userBaseInfo = this.getUserBaseInfoByUserId(userAuth.getUserId());
                if (userBaseInfo == null) {
                    throw new UnknownAccountException("用户不存在");
                }
                return new SimpleAuthenticationInfo(userBaseInfo, userPwd, getName());
            }

        } else {
            //用户名密码登陆
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
