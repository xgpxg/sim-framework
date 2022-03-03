package com.yao2san.sim.system.busi.bean.request;

import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DicCodeItemReq extends Pagination {
    private String dicCode;
    private String dicCodeName;
    private String description;
    private Boolean isShow;
    private String itemCode;
    private String itemText;
    private String itemValue;
}
