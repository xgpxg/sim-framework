package com.yao2san.jvoke.shell;

import com.yao2san.jvoke.base.Callback;
import com.yao2san.jvoke.base.ExecuteResult;
import com.yao2san.jvoke.base.Jvoke;
import com.yao2san.jvoke.common.ThreadUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * windows下调执行shell命令即shell脚本实现(需要安装git的bash工具,并添加到环境变量)
 */
public class GitShellScript implements Jvoke {
    private static final Logger logger = LoggerFactory.getLogger(GitShellScript.class);
    private static final String DEFAULT_CHARSET = "gbk";

    @Override
    public ExecuteResult execute(String command) {
        return execute(Collections.singletonList(command), DEFAULT_CHARSET, null, false, true);
    }

    @Override
    public ExecuteResult execute(List<String> commands) {
        return execute(commands, DEFAULT_CHARSET, null, false, true);
    }

    @Override
    public ExecuteResult execute(String command, Callback callback) {
        return execute(Collections.singletonList(command), DEFAULT_CHARSET, callback, false, true);
    }

    @Override
    public ExecuteResult execute(List<String> commands, Callback callback) {
        return execute(commands, DEFAULT_CHARSET, callback, false, true);
    }

    @Override
    public ExecuteResult execute(String command, String charset, Callback callback) {
        return execute(Collections.singletonList(command), charset, callback, false, true);
    }

    @Override
    public ExecuteResult execute(List<String> commands, String charset, Callback callback, boolean isAsync) {
        return execute(commands, charset, callback, isAsync, true);
    }

    private ExecuteResult execute(List<String> commands, String charset, Callback callback, boolean isAsync, boolean deleteFlag) {
        String scriptContent = String.join("\n", commands);
        scriptContent = replace$(scriptContent);
        if (!commands.get(0).trim().startsWith("#!/bin/bash")) {
            scriptContent = "#!/bin/bash\n" + scriptContent;
        }
        String tempFile = "SHELL_SCRIPT_TEMP_" + System.currentTimeMillis() + ".sh";

        if (isAsync) {
            String finalScriptContent = scriptContent;
            ThreadUtil.FACTORY.submit(() -> {
                exec(Arrays.asList("cd ~ ", "cat > " + tempFile + " <<\\EOF\\\n" + finalScriptContent + "\nEOF\n\nsh " + tempFile + "\n"), charset, callback);
                if (deleteFlag) {
                    exec(Arrays.asList("cd ~ ", "rm -rf " + tempFile), charset, null);
                }
            });
        } else {
            ExecuteResult executeResult = exec(Arrays.asList("cd ~ ", "cat > " + tempFile + " <<\\EOF\\\n\n" + scriptContent + "\nEOF\nsh " + tempFile + "\n"), charset, callback);
            if (deleteFlag) {
                exec(Arrays.asList("cd ~ ", "rm -rf " + tempFile), charset, null);
            }
            return executeResult;
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
        try {
            List<String> lines = FileUtils.readLines(scriptFile, DEFAULT_CHARSET);
            return execute(lines, DEFAULT_CHARSET, callback, isAsync, false);
        } catch (IOException e) {
            e.printStackTrace();
            return new ExecuteResult(1, "", "");
        }
    }

    private ExecuteResult exec(List<String> commands, String charset, Callback callback) {
        if (commands.isEmpty()) {
            logger.debug("command is empty");
            return new ExecuteResult(0, "command is empty", "");
        }
        String userCmd = String.join(" && ", commands);
        List<String> finalCommands = new ArrayList<>();
        finalCommands.add("bash");
        finalCommands.add("-c");
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

    private static String replace$(String content) {
        return content.replace("$", "\\$");
    }
}
