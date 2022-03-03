package com.yao2san.sim.framework.log.core;

public class ESLogAppender extends AbstractLogAppender {
    @Override
    public String getType() {
        return "es";
    }

    @Override
    public void write() {
        super.write();
    }
}
