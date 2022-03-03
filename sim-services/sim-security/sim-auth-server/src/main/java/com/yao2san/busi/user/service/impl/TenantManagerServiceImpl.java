package com.yao2san.busi.user.service.impl;

import com.github.pagehelper.PageInfo;
import com.yao2san.busi.user.bean.request.TenantServiceReq;
import com.yao2san.busi.user.bean.response.TenantServiceRes;
import com.yao2san.busi.user.service.TenantManagerService;
import com.yao2san.sim.framework.web.response.ResponseCode;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TenantManagerServiceImpl extends BaseServiceImpl implements TenantManagerService {
    @Override
    public ResponseData services(TenantServiceReq req) {
        if ("1".equals(req.getIsAuth())) {
            PageInfo<TenantServiceRes> pageInfo = this.qryList("tenantManage.qryAuthServices", req);
            return ResponseData.success(pageInfo);
        } else if ("0".equals(req.getIsAuth())) {
            PageInfo<TenantServiceRes> pageInfo = this.qryList("tenantManage.qryNotAuthServices", req);
            return ResponseData.success(pageInfo);
        } else {
            return ResponseData.error("不支持的服务授权查询类型(期望值:0 or 1，实际值:" + req.getIsAuth() + ")");
        }
    }

    @Override
    public ResponseData serviceAuthorization(TenantServiceReq req) {
        if (req.getUserIds() == null || req.getUserIds().size() == 0) {
            return ResponseData.error(ResponseCode.ILLEGAL_ARGUMENT, "用户ID不能为空");
        }
        if (req.getServices() == null || req.getServices().size() == 0) {
            return ResponseData.error(ResponseCode.ILLEGAL_ARGUMENT, "服务ID不能为空");
        }
        //新增授权
        if ("1".equals(req.getOption())) {
            req.getUserIds().forEach(userId -> {
                Map<String, Object> param = new HashMap<>(2);
                param.put("userId", userId);
                param.put("services", req.getServices());
                //先取消授权
                this.sqlSession.delete("tenantManage.delServicePurview", param);

                req.getServices().forEach(serviceId -> {
                    param.put("serviceId", serviceId);
                    this.sqlSession.insert("tenantManage.addServicePurview", param);
                });
            });
            return ResponseData.success(null,"授权成功");
        }
        //解除授权
        else if ("2".equals(req.getOption())) {
            req.getUserIds().forEach(userId -> {
                Map<String, Object> param = new HashMap<>(2);
                param.put("userId", userId);
                param.put("services", req.getServices());
                this.sqlSession.delete("tenantManage.delServicePurview", param);
            });
            return ResponseData.success(null,"解除授权成功");
        } else {
            return ResponseData.error("不支持的服务授权查询类型(期望值:1 or 2，实际值:" + req.getOption() + ")");
        }


    }
}
