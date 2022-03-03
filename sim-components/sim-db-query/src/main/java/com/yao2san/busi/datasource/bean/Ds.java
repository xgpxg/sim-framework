package com.yao2san.busi.datasource.bean;

import com.yao2san.sim.framework.web.bean.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class Ds extends Pagination {
    /**
     * Datasource id.
     */
    @NotEmpty(message = "datasource id not be empty", groups = DETAIL.class)
    private Long dsId;
    /**
     * Datasource name
     */
    @NotEmpty(message = "datasource id not be empty", groups = ADD.class)
    private String name;
    /**
     * Datasource type:
     * <p>
     * 1: Relational Database(mysql、oracle)
     * <p>
     * 2: redis
     * <p>
     * 3: kafka
     */
    @NotNull(message = "datasource id not be empty", groups = ADD.class)
    private Integer type;

    /**
     * The datasource config. Use json value.
     */
    @NotEmpty(message = "datasource id not be empty", groups = ADD.class)
    private String config;

    private Long groupId = -1L;

    /**
     * Config template id
     */
    private Long dsConfigTemplateId;

    public interface DETAIL {
    }

    public interface UPDATE {
    }

    public interface ADD {
    }

    /**
     * Query filter text
     */
    private String filterText;
}
