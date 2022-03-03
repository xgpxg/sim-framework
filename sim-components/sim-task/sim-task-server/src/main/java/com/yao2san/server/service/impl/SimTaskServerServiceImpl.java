package com.yao2san.server.service.impl;

import com.yao2san.server.bean.LogMsg;
import com.yao2san.server.bean.SimTaskBean;
import com.yao2san.server.service.SimTaskServerService;
import org.springframework.stereotype.Service;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimTaskServerServiceImpl extends BaseServiceImpl implements SimTaskServerService {
    @Override
    public List<SimTaskBean> register(List<SimTaskBean> simTaskBeans) {

        List<SimTaskBean> inc = new ArrayList<>();
        List<SimTaskBean> dec = new ArrayList<>();
        List<SimTaskBean> finalTasks;
        //查询
        List<SimTaskBean> tasks = sqlSession.selectList("task.server.queryTaskList");
        //计算增量
        for (SimTaskBean t : simTaskBeans) {
            if (!tasks.contains(t)) {
                inc.add(t);
            }
        }
        //计算减量
        for (SimTaskBean t : tasks) {
            if (!simTaskBeans.contains(t)) {
                dec.add(t);
            }
        }
        //增量新增
        for (SimTaskBean taskBean : inc) {
            sqlSession.insert("task.server.insertTask", taskBean);
        }
        //减量更新状态
        for (SimTaskBean taskBean : dec) {
            //NULL表示不存在class了
            taskBean.setState("NULL");
            sqlSession.update("task.server.updateTask", taskBean);
        }
        //查询
        finalTasks = sqlSession.selectList("task.server.queryTaskList");

        return finalTasks;
    }

    @Override
    public void saveLog(LogMsg logMsg) {
        String className = logMsg.getJobClassName();
        Map<String, Object> params = new HashMap<>();
        params.put("className", className);
        SimTaskBean simTaskBean = sqlSession.selectOne("task.server.queryTaskList", params);
        logMsg.setSimTaskId(simTaskBean.getId());
        sqlSession.insert("task.server.insertLog", logMsg);
        System.out.println(logMsg.toString());
    }


}
