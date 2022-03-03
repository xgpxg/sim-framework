package com.yao2san.simapiserver.api.service;

import com.github.pagehelper.PageInfo;
import com.yao2san.simapiclient.register.bean.SimApiSimpleInfo;
import com.yao2san.simapiserver.api.bean.ApiQueryBean;
import com.yao2san.simapiserver.server.bean.ApiInfoBean;
import com.yao2san.sim.framework.web.response.ResponseData;

import java.util.List;
import java.util.Map;

public interface SimApi {
    /**
     * 查询API列表
     */
    PageInfo<SimApiSimpleInfo> simApiList(ApiQueryBean apiQueryBean);

    /**
     * 打开或关闭模拟API
     *
     * @param apiId API id
     * @param open  0 关闭 1 打开
     */
    boolean openOrCloseSimulation(Integer apiId, Integer open);

    /**
     * 获取API详情
     */
    ApiInfoBean apiDetail(Integer apiId);

    void updateApi(Integer apiId);

    void deleteApi(Integer apiId);

    List<Map> appList();

    boolean updateApi(ApiQueryBean apiQueryBean);

    /**
     * 获取API模拟数据
     */
    Object simApiData(ApiInfoBean apiInfoBean);

    ResponseData addApi(Map params);

    ResponseData deleteApi(Map params);
}
