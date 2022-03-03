package com.yao2san.sim.framework.log.core;

public class KafkaLogAppender extends AbstractLogAppender {
    @Override
    public String getType() {
        return "kafka";
    }

    @Override
    public void write() {
        //TODO
    }
}
