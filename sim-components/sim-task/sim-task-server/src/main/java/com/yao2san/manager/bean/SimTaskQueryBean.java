package com.yao2san.manager.bean;

import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;

@Data
public class SimTaskQueryBean extends Pagination {

    private Integer id;

    private String group;

    protected String taskName;

    protected String cron;

    private String className;

    private Boolean enabled;

    private String appName;


    private String state;

    private String createDate;
    private String updateDate;
    private String appAddr;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimTaskQueryBean)
            return ((SimTaskQueryBean) obj).getClassName().equals(this.className);
        return false;
    }
}
