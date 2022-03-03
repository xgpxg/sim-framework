package com.yao2san.config;

import com.alibaba.fastjson.JSONObject;
import com.yao2san.busi.user.bean.Permission;
import com.yao2san.busi.user.service.PermissionService;
import com.yao2san.sim.framework.utils.BeanContextUtil;
import com.yao2san.sim.framework.web.response.ResponseCode;
import com.yao2san.sim.framework.web.response.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.AntPathMatcher;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限控制
 */
@Slf4j
@Deprecated
public class PermissionFilter extends AuthorizationFilter {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);

        String requestURL = ((HttpServletRequest) request).getRequestURI();

        PermissionService permissionService = BeanContextUtil.getBean(PermissionService.class);
        List<Permission> urlPermissions = new ArrayList<>();
        List<Permission> allInterfacePermissions = permissionService.getInterfacePermissions();
        boolean match;
        for (Permission permission : allInterfacePermissions) {
            match = PATH_MATCHER.match(permission.getUrl(), requestURL);
            if(match){
                urlPermissions.add(permission);
                break;
            }
        }
        boolean[] permittedArray = subject.isPermitted(urlPermissions.stream().map(Permission::getPurviewCode).toArray(String[]::new));
        if (permittedArray.length == 0) {
            log.debug("Permission is empty, need to grant authorization,request url:{}", requestURL);
        }
        boolean permitted = false;
        for (boolean perm : permittedArray) {
            if (perm) {
                permitted = true;
                break;
            }
        }
        return permitted;
    }

    /**
     * When isAccessAllowed() = false execute
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);

        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        try (PrintWriter out = httpServletResponse.getWriter()) {
            String message = JSONObject.toJSONString(ResponseData.error(ResponseCode.ACCESS_RESTRICTED, "未授权访问"));
            httpServletResponse.setStatus(403);
            out.append(message);
            log.error(message);
        } catch (IOException e) {
            log.error("", e);
        }
        return false;
    }
}
