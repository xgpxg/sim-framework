package com.yao2san.sim.gateway.route.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

@EqualsAndHashCode(callSuper = true)
@Data
public class DynamicRoute extends ZuulProperties.ZuulRoute {

    private String prefix;

    private String routeName;

    private Integer groupId;

    private String version;

    private Integer status;

    private String type;


}
