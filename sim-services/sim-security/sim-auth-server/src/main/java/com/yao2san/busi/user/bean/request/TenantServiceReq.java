package com.yao2san.busi.user.bean.request;

import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TenantServiceReq extends Pagination {
    /**
     * 用户标识
     */
    @NotNull(message = "userId not be null")
    private Long userId;
    /**
     * 是否已授权 0未授权 1已授权
     */
    private String isAuth;

    private String serviceName;

    /**
     * 操作类型: 1新增授权 2取消授权
     */
    private String option;

    private List<Long> services;

    private List<Long> userIds;


}
