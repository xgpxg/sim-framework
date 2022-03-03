package com.yao2san.sim.system.busi.service;

import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.system.busi.bean.request.DicCodeItemReq;

public interface DicCodeItemManagerService {
    ResponseData qryCode(DicCodeItemReq req);
    ResponseData qryItem(DicCodeItemReq req);
}
