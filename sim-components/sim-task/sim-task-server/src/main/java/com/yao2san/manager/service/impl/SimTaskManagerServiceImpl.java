package com.yao2san.manager.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yao2san.manager.bean.SimTaskQueryBean;
import com.yao2san.manager.service.SimTaskManagerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import com.yao2san.sim.framework.utils.RequestUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimTaskManagerServiceImpl extends BaseServiceImpl implements SimTaskManagerService {

    private static final int TASK_LOG_LIST_PAGE_SIZE = 300;

    @Override
    public ResponseData resume(SimTaskQueryBean simTaskBean) {
        SimTaskQueryBean task = sqlSession.selectOne("task.manager.queryTask", simTaskBean);
        String appAddr = task.getAppAddr();
        String url = "http://" + appAddr + "/simTaskClient/resume";
        String result = RequestUtil.postForObject(url, task, String.class);
        if ("1".equals(result)) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", task.getId());
            params.put("state", "NORMAL");
            sqlSession.update("task.manager.updateTask", params);
            return ResponseData.success("恢复成功");
        } else {
            return ResponseData.error("恢复失败:" + result);
        }
    }

    @Override
    public ResponseData suspend(SimTaskQueryBean simTaskBean) {
        SimTaskQueryBean task = sqlSession.selectOne("task.manager.queryTask", simTaskBean);
        String appAddr = task.getAppAddr();
        String url = "http://" + appAddr + "/simTaskClient/suspend";
        String result = RequestUtil.postForObject(url, task, String.class);
        if ("1".equals(result)) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", task.getId());
            params.put("state", "SUSPEND");
            sqlSession.update("task.manager.updateTask", params);
            return ResponseData.success("赞停成功");
        } else {
            return ResponseData.error("赞停失败:" + result);
        }
    }

    @Override
    public ResponseData stop(SimTaskQueryBean simTaskBean) {
        SimTaskQueryBean task = sqlSession.selectOne("task.manager.queryTask", simTaskBean);
        String appAddr = task.getAppAddr();
        String url = "http://" + appAddr + "/simTaskClient/stop";
        String result = RequestUtil.postForObject(url, task, String.class);
        if ("1".equals(result)) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", task.getId());
            params.put("state", "NONE");
            sqlSession.update("task.manager.updateTask", params);
            return ResponseData.success("停止成功");
        } else {
            return ResponseData.error("停止失败:" + result);
        }
    }

    @Override
    public ResponseData start(SimTaskQueryBean simTaskBean) {
        SimTaskQueryBean task = sqlSession.selectOne("task.manager.queryTask", simTaskBean);
        String appAddr = task.getAppAddr();
        String url = "http://" + appAddr + "/simTaskClient/start";
        String result = RequestUtil.postForObject(url, task, String.class);
        if ("1".equals(result)) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", task.getId());
            params.put("state", "NORMAL");
            sqlSession.update("task.manager.updateTask", params);
            return ResponseData.success("启动成功");
        } else {
            return ResponseData.error("启动失败:" + result);
        }

    }


    @Override
    public ResponseData trigger(SimTaskQueryBean simTaskBean) {
        SimTaskQueryBean task = sqlSession.selectOne("task.manager.queryTask", simTaskBean);
        String appAddr = task.getAppAddr();
        String url = "http://" + appAddr + "/simTaskClient/trigger";
        String result = RequestUtil.postForObject(url, task, String.class);
        if ("1".equals(result)) {
            return ResponseData.success("执行成功");
        } else {
            return ResponseData.error("执行失败:" + result);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseData enabled(SimTaskQueryBean simTaskBean) {
        //停止任务
        ResponseData<String> stopRes = this.stop(simTaskBean);
        if (1 == stopRes.getCode()) {
            //更新enabled
            Map<String, Object> params = new HashMap<>();
            params.put("id", simTaskBean.getId());
            params.put("enabled", simTaskBean.getEnabled());
            sqlSession.update("task.manager.updateTask", params);
            return ResponseData.success("禁用成功");
        } else {
            return ResponseData.error("禁用失败");
        }
    }

    @Transactional
    @Override
    public ResponseData refresh(SimTaskQueryBean simTaskBean) {
        //查询数据库中的
        SimTaskQueryBean oldTask = sqlSession.selectOne("task.manager.queryTask", simTaskBean);
        //更新
        sqlSession.update("", simTaskBean);
        //再次查询
        SimTaskQueryBean newTask = sqlSession.selectOne("task.manager.queryTask", simTaskBean);

        String appAddr = newTask.getAppAddr();
        String url = "http://" + appAddr + "/simTaskClient/refresh";
        Map<String, SimTaskQueryBean> params = new HashMap<>();
        params.put("oldTask", oldTask);
        params.put("newTask", newTask);
        String result = RequestUtil.postForObject(url, params, String.class);
        return ResponseData.success(result);
    }

    @Override
    public ResponseData state(SimTaskQueryBean simTaskBean) {
        SimTaskQueryBean task = sqlSession.selectOne("task.manager.queryTask", simTaskBean);
        String appAddr = task.getAppAddr();
        String url = "http://" + appAddr + "/simTaskClient/state";
        String result = RequestUtil.postForObject(url, task, String.class);
        return ResponseData.success(result);
    }

    @Override
    public ResponseData queryTaskList(SimTaskQueryBean simTaskBean) {
        PageHelper.startPage(simTaskBean.getPageNum(), simTaskBean.getPageSize());
        List<SimTaskQueryBean> tasks = sqlSession.selectList("task.manager.queryTaskList", simTaskBean);
        PageInfo<SimTaskQueryBean> pageInfo = new PageInfo<>(tasks);
        return ResponseData.success(pageInfo);
    }

    @Override
    public ResponseData queryTaskLogList(SimTaskQueryBean simTaskQueryBean) {
        PageHelper.startPage(simTaskQueryBean.getPageNum(), TASK_LOG_LIST_PAGE_SIZE);
        List<Map> tasks = sqlSession.selectList("task.manager.queryTaskLogList", simTaskQueryBean);
        PageInfo<Map> pageInfo = new PageInfo<>(tasks);
        return ResponseData.success(pageInfo);
    }
}
