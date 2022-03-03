package com.yao2san.sim.framework.web.bean;

import lombok.Data;

/**
 * @author wxg
 * Base bean
 */
@Data
public class BaseBean {

    private String createDate;
    private String updateDate;

    private Long createUser;
    private Long updateUser;

    private String status;

    private String remark;
}
