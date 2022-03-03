package com.yao2san.sim.auth.client.bean;

import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;

import java.util.List;

/**
 * 用户角色
 */
@Data
public class Role extends Pagination {
    private Long roleId;
    private String roleName;
    private String roleCode;
    private Integer notUpdate;
    private List<Permission> permissions;

    public Role() {

    }

    public Role(Long roleId, String roleCode, String roleName) {
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.roleName = roleName;
    }

}
