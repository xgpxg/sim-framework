package com.yao2san.sim.gateway.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class GrayRouteRes extends Pagination {
    private Long routeGrayId;
    private Long routeId;
    private String serviceId;
    private String grayRouteName;
    private Long groupId;
    private String path;
    private String grayVersion;
    private String mainVersion;
    private double weight;
    private String type;

    private Map<String, Object> headers;
    private Map<String, Object> params;
    private Map<String, Object> cookies;

    private Set<String> serviceInstances;

    @JsonIgnore
    private String headersString;
    @JsonIgnore
    private String paramsString;
    @JsonIgnore
    private String cookiesString;

}
