package com.yao2san.busi.datasource.service.impl;

import com.github.pagehelper.PageInfo;
import com.yao2san.busi.datasource.bean.Ds;
import com.yao2san.busi.datasource.service.DatasourceService;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class DatasourceServiceImpl extends BaseServiceImpl implements DatasourceService {
    @Override
    public ResponseData qryDs(Ds ds) {
        PageInfo<Map<String, Object>> list = this.qryList("ds.qryDs", ds);
        return ResponseData.success(list);
    }


    @Override
    public ResponseData qryDetail(Long dsId) {
        Map<String, Object> ds = this.sqlSession.selectOne("ds.qryDsDetail", dsId);
        return ResponseData.success(ds);
    }

    @Transactional
    @Override
    public ResponseData addDs(Ds ds) {
        this.sqlSession.insert("ds.addDs", ds);
        return ResponseData.success();
    }

    @Override
    public ResponseData updateDs(Ds ds) {
        this.sqlSession.update("ds.updateDs", ds);
        return ResponseData.success(ds);
    }
}
