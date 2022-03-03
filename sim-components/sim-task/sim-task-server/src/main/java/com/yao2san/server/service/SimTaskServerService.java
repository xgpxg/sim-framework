package com.yao2san.server.service;

import com.yao2san.server.bean.LogMsg;
import com.yao2san.server.bean.SimTaskBean;

import java.util.List;

public interface SimTaskServerService {
    List<SimTaskBean> register(List<SimTaskBean> simTaskBeans);

    void saveLog(LogMsg logMsg);
}
