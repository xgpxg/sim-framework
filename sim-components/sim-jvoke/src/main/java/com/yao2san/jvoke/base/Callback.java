package com.yao2san.jvoke.base;

public interface Callback {

    /**
     * 命令或脚本执行成功时
     */
    default void success() {

    }

    /**
     * 命令或脚本执行失败时
     */
    default void error() {

    }

    /**
     * 当有内容输出时
     */
    default void output(String line) {

    }


}
