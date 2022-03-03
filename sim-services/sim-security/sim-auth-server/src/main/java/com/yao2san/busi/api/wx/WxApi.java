package com.yao2san.busi.api.wx;

import com.alibaba.fastjson.JSONObject;
import com.yao2san.config.AppConfig;
import com.yao2san.sim.framework.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信接口API封装
 */
@Component
@Slf4j
public class WxApi {
    /**
     * 登录凭证校验
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html
     * <p>
     * 属性	类型	默认值	必填	说明
     * appid	string		是	小程序 appId
     * secret	string		是	小程序 appSecret
     * js_code	string		是	登录时获取的 code
     * grant_type	string		是	授权类型，此处只需填写 authorization_code
     * </p>
     */
    private static final String CODE_SESSION_API = "https://api.weixin.qq.com/sns/jscode2session";


    @Autowired
    private AppConfig appConfig;

    /**
     * 获取微信用户信息
     *
     * @param code 调用wx.login获取到的code
     */
    public JSONObject getUserInfo(String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("appid", appConfig.getWx().get("appid"));
        params.put("secret", appConfig.getWx().get("secret"));
        params.put("js_code", code);
        params.put("grant_type", appConfig.getWx().get("grant_type"));
        ResponseEntity<String> entity = RequestUtil.getForEntity(CODE_SESSION_API, params, String.class);
        if (entity.getStatusCode() == HttpStatus.OK) {
            JSONObject result = JSONObject.parseObject(entity.getBody());
            log.debug("获取微信用户信息结果:{}", result);
            return result;
        } else {
            log.error("获取微信用户信息接口异常:{}", entity);
        }
        return null;
    }

}
