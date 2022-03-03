package com.yao2san.jvoke.base;

import java.io.File;
import java.util.List;

public interface Jvoke {

    ExecuteResult execute(String command);

    ExecuteResult execute(List<String> commands);

    ExecuteResult execute(String command, Callback callback);

    ExecuteResult execute(List<String> commands, Callback callback);

    ExecuteResult execute(String command, String charset, Callback callback);

    ExecuteResult execute(List<String> commands, String charset, Callback callback, boolean isAsync);

    ExecuteResult execute(File scriptFile);

    ExecuteResult execute(File scriptFile, Callback callback);

    ExecuteResult execute(File scriptFile, Callback callback, boolean isAsync);


}
