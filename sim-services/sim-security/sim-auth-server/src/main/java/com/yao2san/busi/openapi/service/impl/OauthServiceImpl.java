package com.yao2san.busi.openapi.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yao2san.busi.openapi.bean.request.AuthenticationReq;
import com.yao2san.busi.openapi.bean.response.AuthenticationRes;
import com.yao2san.busi.openapi.bean.response.UserTokenInfo;
import com.yao2san.busi.openapi.service.OauthService;
import com.yao2san.busi.user.bean.User;
import com.yao2san.sim.framework.utils.redisUtil.RedisUtil;
import com.yao2san.sim.framework.web.exception.BusiException;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OauthServiceImpl extends BaseServiceImpl implements OauthService {

    private static final Integer TOKEN_EXPIRE_TIME = 24;


    @Override
    public ResponseData<AuthenticationRes> authenticate(AuthenticationReq authenticationReq) {
        //验证openId和secretKey

        //校验通过后 获取用户信息
        User user;
        try {
            user = this.getUser(authenticationReq);
        } catch (BusiException e) {
            return ResponseData.error(e.getMessage());
        }
        if (user == null) {
            return ResponseData.error("租户信息获取异常");
        }

        //生成token
        String token = this.createToken();
        //缓存
        this.cache(user.getUserId(), authenticationReq.getOpenId(), token);
        //过期时间
        DateTime expireDate = DateUtil.offsetHour(new Date(), TOKEN_EXPIRE_TIME);
        AuthenticationRes res = new AuthenticationRes(token, DateUtil.formatDateTime(expireDate));

        return ResponseData.success(res);
    }

    private User getUser(AuthenticationReq authenticationReq) {
        User user = this.sqlSession.selectOne("oauth.qryUserByOpenId", authenticationReq.getOpenId());
        if (user == null) {
            throw new BusiException("租户不存在");
        }
        String secretKey = this.sqlSession.selectOne("oauth.qrySecretKyByUserId", user.getUserId());
        //TODO 解密
        if (!StringUtils.equalsIgnoreCase(secretKey, authenticationReq.getSecretKey())) {
            throw new BusiException("认证失败");
        }
        return user;
    }

    /**
     * 缓存用户信息
     *
     * @param userId 用户标识
     */
    private void cache(Long userId, String openId, String token) {
        UserTokenInfo userTokenInfo = new UserTokenInfo();

        //获取基础信息
        User user = this.sqlSession.selectOne("oauth.qryTenantUser", openId);
        //获取已授权服务
        List<Map<String, Object>> serviceList = this.sqlSession.selectList("oauth.qryPermissionService", userId);
        serviceList.forEach(s -> {
            UserTokenInfo.Service service = new UserTokenInfo.Service();
            service.setServiceId(MapUtils.getLong(s, "serviceId"));
            service.setName(MapUtils.getString(s, "name"));
            service.setUrl(MapUtils.getString(s, "url"));
            userTokenInfo.getServices().add(service);
        });

        //基本信息
        userTokenInfo.setOpenId(user.getOpenId());
        userTokenInfo.setUserId(user.getUserId());
        userTokenInfo.setUserName(user.getUserName());
        //用户属性 TODO
        userTokenInfo.setAttr(new LinkedHashMap<>());

        String value = JSONObject.toJSONString(userTokenInfo, SerializerFeature.WriteNullStringAsEmpty);

        log.debug("授权信息:{}",value);
        RedisUtil.set(RedisUtil.TOKEN_KEY_PREFIX + token, value, TOKEN_EXPIRE_TIME, TimeUnit.HOURS);

        //TODO 保存到数据库
        //TODO 当授权信息变更时 更新数据库中的token 从redis中删除旧的token 强制token失效 重新认证
    }

    private String createToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString().replace("-", ""));
    }
}
