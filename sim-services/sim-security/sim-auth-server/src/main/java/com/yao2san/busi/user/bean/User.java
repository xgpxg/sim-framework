package com.yao2san.busi.user.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class User extends Pagination {
    /**
     * 用户ID
     */
    @NotNull(groups = Update.class, message = "用户标识不能为空")
    private Long userId;
    /**
     * 用户名
     */
    @NotEmpty(groups = Add.class, message = "用户名不能为空")
    private String userName;
    /**
     * 用户联系号码
     */
    @NotEmpty(groups = Add.class, message = "电话号码不能为空")
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    @JsonIgnore
    private transient String password;

    @NotEmpty(groups = Add.class, message = "用户类型不能为空")
    private String userType;

    private String effDate;

    private String expDate;

    //private List<Role> roles;

    private Integer notUpdate;

    private String description;

    private String status;

    @JsonIgnore
    private String filterText;
    @JsonIgnore
    private String isAuthorized;

    private String openId;

    private String secretKey;

    public interface Add {
    }

    public interface Update {
    }

}
