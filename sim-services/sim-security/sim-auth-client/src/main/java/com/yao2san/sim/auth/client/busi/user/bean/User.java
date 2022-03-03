package com.yao2san.sim.auth.client.busi.user.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class User extends Pagination {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 用户联系号码
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    private String password;

    private String userType;

    private String effDate;

    private String expDate;

    private List<Role> roles;

    private Integer notUpdate;

    @JsonIgnore
    private String filterText;
    @JsonIgnore
    private String isAuthorized;

}
