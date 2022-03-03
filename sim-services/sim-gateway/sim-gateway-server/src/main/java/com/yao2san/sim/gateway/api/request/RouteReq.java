package com.yao2san.sim.gateway.api.request;

import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class RouteReq extends Pagination {

    @NotNull(message = "routeId can not be null", groups = {Update.class})
    private Long routeId;

    @NotEmpty(message = "routeName can not be empty", groups = {Add.class})
    private String routeName;

    @NotNull(message = "groupId can not be null", groups = {Add.class})
    private Integer groupId;

    private String version;

    private String status;

    private String type;

    private String prefix;

    @NotEmpty(message = "path can not be empty", groups = {Add.class})
    private String path;

    private String serviceId;

    private String url;

    private boolean stripPrefix = true;

    private boolean retryable = false;

    private Set<String> sensitiveHeaders;

    private boolean customSensitiveHeaders = false;

    /**
     * query param
     */
    private String filterText;

    public interface Add {
    }

    public interface Update {
    }
}
