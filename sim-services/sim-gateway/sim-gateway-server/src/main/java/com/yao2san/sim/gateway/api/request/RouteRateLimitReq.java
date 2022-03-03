package com.yao2san.sim.gateway.api.request;

import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class RouteRateLimitReq extends Pagination {

    @NotNull(message = "routeId can not be null", groups = {Add.class, Delete.class, Update.class})
    private Long routeId;

    @NotNull(message = "routeRateLimitPolicyId can not be null", groups = {Update.class, Delete.class})
    private Long routeRateLimitPolicyId;

    @NotNull(message = "refreshInterval can not be null", groups = {Add.class})
    private Long refreshInterval;

    @NotNull(message = "limit can not be null", groups = {Add.class})
    private Long limit;

    private Long quota;

    private boolean breakOnMatch = false;

    @NotNull(message = "matchType can not be null", groups = {Add.class})
    private String matchType;

    private String matcher;

    public interface Add {
    }

    public interface Update {
    }

    public interface Delete {
    }

}
