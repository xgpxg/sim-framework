package com.yao2san.simapiserver.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.yao2san.simapiclient.core.util.RestTemplateUtil;
import com.yao2san.simapiclient.register.bean.SimApiSimpleInfo;
import com.yao2san.simapiserver.api.bean.ApiQueryBean;
import com.yao2san.simapiserver.api.service.SimApi;
import com.yao2san.simapiserver.server.bean.ApiInfoBean;
import org.apache.commons.collections4.MapUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.yao2san.sim.framework.web.response.ResponseData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimApiImpl implements SimApi {
    private static final Logger logger = LoggerFactory.getLogger(SimApiImpl.class);
    @Autowired
    private SqlSessionTemplate session;

    @Override
    public PageInfo<SimApiSimpleInfo> simApiList(ApiQueryBean apiQueryBean) {
        PageHelper.startPage(apiQueryBean.getPagination().getPageNum(), apiQueryBean.getPagination().getPageSize());
        List<SimApiSimpleInfo> list = session.selectList("simapi.simApiList", apiQueryBean);
        PageInfo<SimApiSimpleInfo> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public boolean openOrCloseSimulation(Integer apiId, Integer open) {
        //通知API所属应用刷新API列表缓存
        String res = notifyRefresh(apiId);
        if("1".equals(res)){
            Map<String, Object> params = new HashMap<>();
            params.put("apiId", apiId);
            params.put("open", open);
            session.update("server.openOrCloseSimulation", params);
            return true;
        }
        return false;
    }

    @Override
    public ApiInfoBean apiDetail(Integer apiId) {
        return session.selectOne("simapi.apiDetailById", apiId);
    }

    @Override
    public void updateApi(Integer apiId) {

    }

    @Override
    public void deleteApi(Integer apiId) {

    }

    @Override
    public List<Map> appList() {
        return session.selectList("simapi.appList");
    }

    @Override
    public boolean updateApi(ApiQueryBean apiQueryBean) {
        return session.update("simapi.updateApi",apiQueryBean)==1;
    }

    @Override
    public Object simApiData(ApiInfoBean apiInfoBean) {
        return session.selectOne("simapi.simApiData", apiInfoBean);
    }

    @Override
    public ResponseData addApi(Map params) {
        int count = session.selectOne("simapi.checkExists",params);
        if(count>0){
            return ResponseData.error(MapUtils.getString(params,"apiUrl")+"已存在");
        }else {
            count = session.insert("server.addApi",params);
            if(count==1){
                return ResponseData.success();
            }
        }
        return ResponseData.error("添加失败");
    }

    /**
     * 通知客户端刷新api列表(只刷新api归属应用的api列表)
     *
     * @param apiId apiId
     */
    private String notifyRefresh(Integer apiId) {
        ApiInfoBean apiInfoBean = apiDetail(apiId);
        String appAddr = apiInfoBean.getAppAddr();
        String url = "http://" + appAddr + "/sim-api/refresh";
        String res="0";
        try{
            res = RestTemplateUtil.post(url, String.class).getBody();
            logger.info("{} refresh success",apiInfoBean.getAppName());
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public ResponseData deleteApi(Map params) {
        int count = session.delete("simapi.deleteApi",params);
        if(count==1){
            return ResponseData.success();
        }else {
            return ResponseData.error("删除失败");
        }
    }

    /**
     * 通知客户端刷新api列表(刷新所有应用)
     */
    private void notifyRefreshAll() {
        List<Map<String, Object>> appList = session.selectList("server.appList");
        for (Map<String, Object> map : appList) {
            String appAddr = MapUtils.getString(map, "appAddr", "");
            if (!StringUtils.isEmpty(appAddr)) {
                String url = "http://" + appAddr + "/sim-api/refresh";
                String res = RestTemplateUtil.post(url, String.class).getBody();
                logger.info(res);
            }
        }
    }
}
