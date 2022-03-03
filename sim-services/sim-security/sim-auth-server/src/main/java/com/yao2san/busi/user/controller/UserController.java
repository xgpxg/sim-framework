package com.yao2san.busi.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.yao2san.busi.api.wx.WxApi;
import com.yao2san.busi.user.bean.User;
import com.yao2san.busi.user.service.UserService;
import com.yao2san.config.AppConfig;
import com.yao2san.sim.framework.utils.CacheUtil;
import com.yao2san.sim.framework.web.response.ResponseData;
import lombok.extern.slf4j.Slf4j;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppConfig appConfig;
    @Autowired
    private WxApi wxApi;

    /**
     * 获取用户基本信息
     *
     * @param token token进行验证
     * @param scope base/role/purview/all
     */
    @GetMapping("/token")
    public ResponseData getUserBaseInfo(String token,@RequestParam(required = false,defaultValue = "") String scope) {
        return userService.getUserInfo(token,scope);
    }

    @PatchMapping
    public ResponseData updateUserInfo(@RequestBody User user) {
        return userService.updateCurrUserInfo(user);
    }

    /**
     * 生成授权二维码
     *
     * @param random 10位随机数
     */
    @GetMapping("/wx/authQrCode")
    public void getWxAuthQrCode(String random, HttpServletResponse response) throws IOException {
        String redirectUrl = URLEncoder.encode(appConfig.getWx().get("auth-redirect-url"));
        String appId = appConfig.getWx().get("appid");
        if (StringUtils.isEmpty(redirectUrl)) {
            throw new RuntimeException("redirectUrl不能为空");
        }
        if (StringUtils.isEmpty(appId)) {
            throw new RuntimeException("appId不能为空");
        }
        if (StringUtils.isEmpty(random)) {
            throw new RuntimeException("random不能为空");
        }
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId + "&redirect_uri=" + redirectUrl + "&response_type=code&scope=snsapi_userinfo&state=" + random + "&connect_redirect=1#wechat_redirect";
        log.info("授权URL:{}", url);
        ByteArrayOutputStream out = QRCode.from(url).to(ImageType.PNG).stream();

        response.setContentType("image/png");
        response.setContentLength(out.size());

        OutputStream outStream = response.getOutputStream();

        outStream.write(out.toByteArray());

        outStream.flush();
        outStream.close();

    }

    /**
     * 授权状态轮询接口
     */
    @GetMapping("/wx/auth")
    public ResponseData getWxUserAuthInfo(String random) {
        return ResponseData.success(CacheUtil.get("WX_AUTH_" + random));
    }

    /**
     * 微信授权重定向接口
     *
     * @param code  微信code
     * @param state 请求授权时携带的随机数参数
     * @return
     */
    @GetMapping("/wx/redirect")
    public ResponseData redirect(String code, String state) {
        //换取access_token
        JSONObject userInfo = wxApi.getUserInfo(code);
        //写入缓存 有效期5分钟
        //TODO 模拟
        String openid = "1234567890";//userInfo.getString("openid");
        CacheUtil.put("WX_AUTH_" + state, openid, 300);

        return ResponseData.success();
    }
}
