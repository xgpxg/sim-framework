package com.yao2san.sim.gateway.api.request;

import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class GrayRouteReq extends Pagination {
    private Long routeGrayId;

    @NotNull(message =  "routeId can not be null", groups = {Add.class})
    private Long routeId;

    @NotEmpty(message = "grayRouteName can not be null", groups = {Add.class})
    private String grayRouteName;

    @NotNull(message = "serviceId can not be null", groups = {Add.class})
    private String serviceId;

    @NotNull(message = "path can not be null", groups = {Add.class})
    private String path;

    @NotNull(message = "grayVersion can not be null", groups = {Add.class})
    private String grayVersion;

    @NotNull(message = "mainVersion can not be null", groups = {Add.class})
    private String mainVersion;

    @NotNull(message = "type can not be null", groups = {Add.class})
    private String type;

    private Long groupId;

    private double weight;

    private Map<String, Object> headers;
    private Map<String, Object> params;
    private Map<String, Object> cookies;

    @NotNull(message = "serviceInstances can not be null", groups = {Add.class})
    private Set<String> serviceInstances;

    private String headersString;
    private String paramsString;
    private String cookiesString;

    public interface Add {
    }

    public interface Update {
    }
}
