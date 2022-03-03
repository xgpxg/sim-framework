package com.yao2san.busi.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yao2san.busi.api.wx.WxApi;
import com.yao2san.busi.constant.SystemConstant;
import com.yao2san.busi.user.bean.User;
import com.yao2san.busi.user.bean.UserAuth;
import com.yao2san.busi.user.service.SecurityService;
import com.yao2san.busi.user.service.UserService;
import com.yao2san.sim.framework.utils.CommonUtil;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import com.yao2san.utils.ThreadUtil;
import com.yao2san.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SecurityServiceImpl extends BaseServiceImpl implements SecurityService {
    @Autowired
    private UserService userService;

    @Autowired
    private WxApi wxApi;

    @Override
    @Transactional
    public ResponseData login(Map<String, Object> params) {
        final String loginType = MapUtils.getString(params, "type");
        if (StringUtils.isEmpty(loginType)) {
            return ResponseData.businessError("不支持的登陆方式");
        }
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = null;
        try {

            switch (loginType) {
                //用户名密码登陆
                case "0":
                    final String un = MapUtils.getString(params, "username");
                    final String pwd = MapUtils.getString(params, "password");
                    token = new UsernamePasswordToken(un, pwd);
                    token.setRememberMe(true);
                    subject.login(token);
                    break;
                //手机号登陆
                case "1":
                    final String phone = MapUtils.getString(params, "phone");
                    String vCode = MapUtils.getString(params, "vCode");
                    //加前缀 区分是否是验证码
                    vCode = "vCode_" + vCode;
                    token = new UsernamePasswordToken(phone, vCode);
                    token.setRememberMe(true);
                    subject.login(token);
                    break;
                //微信登陆 入参传wx.login获取到的code
                case "2":
                    final String code = MapUtils.getString(params, "code");
                    //微信接口获取openid
                    JSONObject userInfo = wxApi.getUserInfo(code);
                    String openid = userInfo.getString("openid");
                    if (StringUtils.isNotEmpty(openid)) {
                        token = new UsernamePasswordToken(openid, "openid_" + openid);
                        token.setRememberMe(true);
                        subject.login(token);
                    }else {
                        throw new AccountException("openid授权验证失败");
                    }
                    break;
                default:
                    return ResponseData.error("不支持的登陆方式");
            }

        } catch (UnknownAccountException e) {
            if (StringUtils.equals("1", loginType)) {
                //未注册手机号直接注册
                final String phone = MapUtils.getString(params, "phone");
                //创建用户信息
                this.addNewUser(phone);
                //注册成功 调用登录
                String vCode = MapUtils.getString(params, "vCode");
                //加前缀 区分是否是验证码
                vCode = "vCode_" + vCode;
                token = new UsernamePasswordToken(phone, vCode);
                token.setRememberMe(true);
                subject.login(token);
                log.info("新用户注册成功");
            } else {
                log.info("该用户未注册");
                return ResponseData.businessError("该用户未注册");
            }

        } catch (AccountException e) {
            return ResponseData.businessError(e.getMessage());
        }
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("token", UserUtil.getCurrUserToken());

        return ResponseData.success(tokenMap);
    }

    @Override
    public boolean checkAuthExists(String openid, SystemConstant.LoginType loginType) {
        UserAuth userAuth = new UserAuth();
        userAuth.setOpenid(openid);
        userAuth.setLoginType(loginType.getTypeName());
        Integer count = this.sqlSession.selectOne("user.checkAuthExists", userAuth);
        return count > 0;
    }

    @Override
    @Transactional
    public UserAuth addUserAuth(UserAuth userAuth) {
        this.sqlSession.insert("user.addUserAuth", userAuth);
        return userAuth;
    }

    @Override
    @Transactional
    public void updateUserAuth(UserAuth userAuth) {
        this.sqlSession.update("user.updateUserAuth", userAuth);
    }

    @Override
    public UserAuth qryUserAuth(String openId ,SystemConstant.LoginType loginType) {
        UserAuth userAuth = new UserAuth();
        userAuth.setOpenid(openId);
        userAuth.setLoginType(loginType.getTypeName());
        return this.sqlSession.selectOne("user.qryUserAuth", userAuth);
    }

    private User addNewUser(String phone) {
        String defaultUsername = CommonUtil.getRandomString(8);
        User user = new User();
        user.setPhone(phone);
        user.setUserName(defaultUsername);
        this.sqlSession.insert("user.addUser", user);
        Map<String, List<Object>> attr = new HashMap<>();
        //attr.put("USER_BALANCE", Collections.singletonList(0));
        //attr.put("USER_WITHDRAWN_AMOUNT", Collections.singletonList(0));
        userService.upsertUserAttr(user.getUserId(), attr);

        //异步初始化其他用户信息
        ThreadUtil.submit(() -> {


        });

        return user;
    }
}
