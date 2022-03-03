package com.yao2san.busi.user.bean.response;

import com.yao2san.sim.framework.web.bean.BaseBean;
import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TenantServiceRes extends BaseBean {
    private Long servicePurviewId;
    private Long serviceId;
    private Long userId;
    private String serviceName;
    private String serviceType;
    private String isAuth;
    private String url;
    private String effDate;

}
