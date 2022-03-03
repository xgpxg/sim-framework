package com.yao2san.sim.auth.client.filter;

import com.alibaba.fastjson.JSONObject;
import com.yao2san.sim.framework.utils.redisUtil.RedisUtil;
import com.yao2san.sim.framework.web.response.ResponseCode;
import com.yao2san.sim.framework.web.response.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * open api, check token
 */
@Component
@WebFilter(filterName = "tokenFilter")
@Slf4j
public class TokenFilter implements Filter {
    private static final AntPathMatcher MATCHER = new AntPathMatcher();
    /**
     * 对外接口访问前缀 只有该路径下的请求才会被拦截
     */
    private static final String PATTERN = "/service/**";
    /**
     * header中token的key
     */
    private static final String TOKEN = "token";

    private static final String[] WHITE_LIST = {"/service/**/oauth/token"};

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        //非法请求校验
        String checkResult = this.requestCheck(request);
        if (StringUtils.isNotEmpty(checkResult)) {
            PrintWriter writer = servletResponse.getWriter();
            writer.write(JSONObject.toJSONString(ResponseData.error(ResponseCode.ILLEGAL_ARGUMENT, checkResult)));
            writer.flush();
            return;
        }
        String url = request.getRequestURI();
        if (this.shouldFilter(url)) {
            String token;
            token = request.getHeader(TOKEN);
            token = StringUtils.isEmpty(token) ? request.getParameter("token") : token;

            servletResponse.setCharacterEncoding("utf-8");

            String checkToken = this.checkToken(token);
            if (StringUtils.isNotEmpty(checkToken)) {
                PrintWriter writer = servletResponse.getWriter();
                writer.write(JSONObject.toJSONString(ResponseData.error(ResponseCode.ILLEGAL_ARGUMENT, checkToken)));
                writer.flush();
                return;
            }
            if (!hasPermission(token, url)) {
                PrintWriter writer = servletResponse.getWriter();
                writer.write(JSONObject.toJSONString(ResponseData.error(ResponseCode.ACCESS_RESTRICTED, "您访问的接口不存在或没有权限访问")));
                writer.flush();
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * 请求校验(特殊字符、非法参数)
     */
    private String requestCheck(HttpServletRequest request) {
        if (request.getRequestURI().contains("*")) {
            return "Illegal request";
        }
        return "";
    }

    private boolean shouldFilter(String url) {
        //must match
        boolean pass = MATCHER.match(PATTERN, url);
        if (!pass) {
            return false;
        }
        for (String path : WHITE_LIST) {
            if (MATCHER.match(path, url)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasPermission(String token, String url) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        //从缓存中取用户信息
        UserTokenInfo info = RedisUtil.get(RedisUtil.TOKEN_KEY_PREFIX + token, UserTokenInfo.class);
        if (info == null || info.getUserId() == null) {
            return false;
        }
        List<UserTokenInfo.Service> services = info.getServices();
        AtomicBoolean isPass = new AtomicBoolean(false);
        services.forEach(service -> {
            if (MATCHER.match(service.getUrl(), url)) {
                isPass.set(true);
            }
        });
        return isPass.get();
    }

    /**
     * 校验token合法性
     *
     * @param token token
     */
    private String checkToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return "Missing token";
        }
        if (token.length() != 32) {
            return "Invalid token";
        }
        return "";
    }


}
