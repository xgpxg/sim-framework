package com.yao2san.jvoke.base;

import lombok.Data;

@Data
public class ExecuteResult {
    private int code;
    private String data;
    private String lastLine;

    public ExecuteResult() {

    }

    public ExecuteResult(int code, String data, String lastLine) {
        this.code = code;
        this.data = data;
        this.lastLine = lastLine;
    }


}
