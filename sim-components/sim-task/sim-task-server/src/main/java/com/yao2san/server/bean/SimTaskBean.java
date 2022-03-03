package com.yao2san.server.bean;

import lombok.Data;

@Data
public class SimTaskBean {

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
        if (obj instanceof SimTaskBean)
            return ((SimTaskBean) obj).getClassName().equals(this.className);
        return false;
    }
}
