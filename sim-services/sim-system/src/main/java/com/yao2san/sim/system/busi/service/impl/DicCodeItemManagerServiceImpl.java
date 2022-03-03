package com.yao2san.sim.system.busi.service.impl;

import com.github.pagehelper.PageInfo;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import com.yao2san.sim.system.busi.bean.request.DicCodeItemReq;
import com.yao2san.sim.system.busi.bean.response.DicCodeItemRes;
import com.yao2san.sim.system.busi.service.DicCodeItemManagerService;
import org.springframework.stereotype.Service;

@Service
public class DicCodeItemManagerServiceImpl extends BaseServiceImpl implements DicCodeItemManagerService {

    public ResponseData qryCode(DicCodeItemReq req) {
        PageInfo<DicCodeItemRes> list = this.qryList("dicCodeItem.qryDicCode", req);
        return ResponseData.success(list);
    }

    @Override
    public ResponseData qryItem(DicCodeItemReq req) {
        PageInfo<DicCodeItemRes> list = this.qryList("dicCodeItem.qryDicItem", req);
        return ResponseData.success(list);
    }
}
