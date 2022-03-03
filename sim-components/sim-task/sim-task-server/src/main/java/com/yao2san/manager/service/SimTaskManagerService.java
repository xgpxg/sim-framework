package com.yao2san.manager.service;

import com.yao2san.manager.bean.SimTaskQueryBean;
import com.yao2san.sim.framework.web.response.ResponseData;

public interface SimTaskManagerService {
    ResponseData resume(SimTaskQueryBean simTaskBean);

    ResponseData suspend(SimTaskQueryBean simTaskBean);

    ResponseData stop(SimTaskQueryBean simTaskBean);
    ResponseData start(SimTaskQueryBean simTaskBean);

    ResponseData trigger(SimTaskQueryBean simTaskBean);

    ResponseData enabled(SimTaskQueryBean simTaskBean);

    /**
     * 刷新(修改任务名称或分组时触发)
     */
    ResponseData refresh(SimTaskQueryBean simTaskBean);

    ResponseData state(SimTaskQueryBean simTaskBean);

    ResponseData queryTaskList(SimTaskQueryBean simTaskQueryBean);

    ResponseData queryTaskLogList(SimTaskQueryBean simTaskQueryBean);
}

    

