package com.yao2san.jvoke.shell;

import com.yao2san.jvoke.base.Callback;
import com.yao2san.jvoke.base.ExecuteResult;
import com.yao2san.jvoke.base.Jvoke;
import com.yao2san.jvoke.common.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

/**
 * 执行本地shell命令
 */
public class ShellScript implements Jvoke {
    private static final Logger logger = LoggerFactory.getLogger(ShellScript.class);
    private static final String DEFAULT_CHARSET = "gbk";

    @Override
    public ExecuteResult execute(String command) {
        return execute(Collections.singletonList(command), DEFAULT_CHARSET, null, false);
    }

    @Override
    public ExecuteResult execute(List<String> commands) {
        return execute(commands, DEFAULT_CHARSET, null, false);
    }

    @Override
    public ExecuteResult execute(String command, Callback callback) {
        return execute(Collections.singletonList(command), DEFAULT_CHARSET, callback, false);
    }

    @Override
    public ExecuteResult execute(List<String> commands, Callback callback) {
        return execute(commands, DEFAULT_CHARSET, callback, false);
    }

    @Override
    public ExecuteResult execute(String command, String charset, Callback callback) {
        return execute(Collections.singletonList(command), charset, callback, false);
    }

    @Override
    public ExecuteResult execute(List<String> commands, String charset, Callback callback, boolean isAsync) {
        if (isAsync) {
            ThreadUtil.FACTORY.submit(() -> {
                exec(commands, charset, callback);
            });
        } else {
            return exec(commands, charset, callback);
        }
        return new ExecuteResult(0, "", "");
    }

    @Override
    public ExecuteResult execute(File scriptFile) {
        return execute(scriptFile, null, false);
    }

    @Override
    public ExecuteResult execute(File scriptFile, Callback callback) {
        return execute(scriptFile, callback, false);
    }

    @Override
    public ExecuteResult execute(File scriptFile, Callback callback, boolean isAsync) {
        List<String> cmds = null;
        try {
            cmds = Collections.singletonList("sh " + scriptFile.getCanonicalPath());
            if (isAsync) {
                List<String> finalCmds = cmds;
                ThreadUtil.FACTORY.submit(() -> {
                    exec(finalCmds, DEFAULT_CHARSET, callback);
                });
            } else {
                return exec(cmds, DEFAULT_CHARSET, callback);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ExecuteResult(0, "", "");
    }

    private ExecuteResult exec(List<String> commands, String charset, Callback callback) {
        if (commands.isEmpty()) {
            logger.debug("command is empty");
            return new ExecuteResult(0, "command is empty", "");
        }
        String userCmd = String.join(" && ", commands);
        List<String> finalCommands = new ArrayList<>();
        finalCommands.add(userCmd);

        ExecuteResult result = new ExecuteResult();
        InputStream is = null;
        try {
            Process process = new ProcessBuilder(finalCommands).redirectErrorStream(true).start();
            is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line).append(System.lineSeparator());
                if (callback != null) {
                    callback.output(line);
                }
                result.setLastLine(line);
            }
            int code = process.waitFor();
            result.setCode(code);
            result.setData(data.toString().trim());
            if (callback != null) {
                if (code == 0) {
                    callback.success();
                } else {
                    callback.error();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeStream(is);
        }
        return result;
    }

    private static void closeStream(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }
}
