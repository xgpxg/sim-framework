package com.yao2san.busi.user.bean;

import com.yao2san.sim.framework.web.bean.BaseBean;
import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Permission extends Pagination {
    private Long purviewId;

    @NotEmpty(message = "权限名称不能为空", groups = ADD.class)
    private String purviewName;
    @NotEmpty(message = "权限编码不能为空", groups = ADD.class)
    private String purviewCode;
    /**
     * 权限类型 1菜单权限 2接口权限 3页面元素
     */
    @NotEmpty(message = "权限类型不能为空(可选值:1,2,3)", groups = ADD.class)
    private String purviewType;
    @NotEmpty(message = "url不能为空", groups = ADD.class)
    private String url;
    private String icon;
    private String component;
    private Long  parentId;
    private String effDate;
    private String expDate;
    private Integer orderNum;
    private Boolean hidden;
    private String path;
    ///////查询参数////////
    private Long userId;
    private String filterText;
    /**
     * 是否被授权
     */
    private String isPermissions;
    /**
     * 授权对象
     */
    private String purviewObject;

    public interface ADD{}
}
