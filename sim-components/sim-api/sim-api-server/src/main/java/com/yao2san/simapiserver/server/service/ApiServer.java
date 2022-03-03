package com.yao2san.simapiserver.server.service;


import com.yao2san.simapiclient.register.bean.ApiRegister;
import com.yao2san.simapiclient.register.bean.SimApiSimpleInfo;
import com.yao2san.simapiserver.server.bean.ApiInfoBean;

import java.util.List;

public interface ApiServer {
    /**
     * 注册api
     */
    void addApi(List<ApiRegister> apiRegisters);

    /**
     * 查询API列表
     */
    List<SimApiSimpleInfo> simApiList(ApiInfoBean apiInfoQueryBean);

    /**
     * 获取API模拟数据
     */
    Object simApiData(ApiInfoBean apiInfoQueryBean);

    /**
     * 打开或关闭模拟API
     *
     * @param apiId API id
     * @param open  0 关闭 1 打开
     */
    int openOrCloseSimulation(Integer apiId, Integer open);

    /**
     * 获取API详情
     */
    ApiInfoBean apiDetail(Integer apiId);

    void updateApi(Integer apiId);
    void deleteApi(Integer apiId);

}
