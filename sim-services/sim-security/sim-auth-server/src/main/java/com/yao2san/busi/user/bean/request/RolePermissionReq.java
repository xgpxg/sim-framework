package com.yao2san.busi.user.bean.request;

import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RolePermissionReq extends Pagination {
    private Long roleId;

    private String isPermissions;

    private Long purviewId;

    private String purviewName;
}
