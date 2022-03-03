package com.yao2san.sim.common.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wxg
 */
@Data
public class UserPrincipal implements Serializable {
    private Long id;

    private Long userId;

    private String userName;

    private String phone;

    private String email;

    @JsonIgnore
    private String password;

}
