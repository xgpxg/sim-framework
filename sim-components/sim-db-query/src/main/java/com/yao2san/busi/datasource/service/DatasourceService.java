package com.yao2san.busi.datasource.service;

import com.yao2san.busi.datasource.bean.Ds;
import com.yao2san.sim.framework.web.response.ResponseData;

/**
 * 数据源管理接口
 */
public interface DatasourceService {
    /**
     * Query datasource list
     */
    ResponseData qryDs(Ds ds);

    /**
     * Query datasource detail
     */
    ResponseData qryDetail(Long dsId);

    /**
     * Add new datasource
     */
    ResponseData addDs(Ds ds);

    /**
     * Update datasource
     */
    ResponseData updateDs(Ds ds);
}
