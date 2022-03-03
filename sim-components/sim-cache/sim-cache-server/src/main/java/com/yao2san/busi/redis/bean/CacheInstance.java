package com.yao2san.busi.redis.bean;

import com.yao2san.sim.framework.web.bean.BaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CacheInstance extends BaseBean {
    private Long cacheInstanceId;

    private String name;

    private String type;

    private String connectType;

    private String host;

    private Integer port;

    private String nodes;
}
