package com.yao2san.busi.datasource.controller;

import com.yao2san.busi.datasource.bean.Ds;
import com.yao2san.busi.datasource.service.DatasourceService;
import com.yao2san.sim.framework.web.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Datasource rest api.
 */
@RestController
@RequestMapping("dsm")
public class DatasourceManage {
    @Autowired
    private DatasourceService datasourceService;

    /**
     * Query datasource list
     */
    @GetMapping
    public ResponseData qryDs(Ds ds) {
        return datasourceService.qryDs(ds);
    }

    /**
     * Query datasource detail
     */
    @GetMapping("/{dsId}")
    public ResponseData qryDsDetail(@PathVariable Long dsId) {
        return datasourceService.qryDetail(dsId);
    }

    /**
     * Add new datasource
     */
    @PostMapping
    public ResponseData addFs(@Validated(Ds.ADD.class) @RequestBody Ds ds) {
        return datasourceService.addDs(ds);
    }

    /**
     * Update datasource
     */
    @PatchMapping
    public ResponseData updateDs(@Validated(Ds.UPDATE.class) @RequestBody Ds ds) {
        return datasourceService.updateDs(ds);
    }
}
