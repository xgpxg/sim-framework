package com.yao2san.sim.framework.web.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wxg
 * 分页
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Pagination extends BaseBean {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
