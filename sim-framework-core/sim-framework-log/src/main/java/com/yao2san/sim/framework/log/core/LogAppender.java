package com.yao2san.sim.framework.log.core;

public interface LogAppender {
    String getType();

    void write();
}
