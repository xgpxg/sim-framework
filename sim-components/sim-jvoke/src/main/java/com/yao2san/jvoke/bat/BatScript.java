package com.yao2san.jvoke.bat;

import com.yao2san.jvoke.base.Callback;
import com.yao2san.jvoke.base.ExecuteResult;
import com.yao2san.jvoke.base.Jvoke;
import com.yao2san.jvoke.common.ThreadUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * windows下调执行cmd命令即bat脚本实现
 */
public class BatScript implements Jvoke {
    private static final Logger logger = LoggerFactory.getLogger(BatScript.class);
    private static final String DEFAULT_CHARSET = "gbk";

    @Override
    public ExecuteResult execute(String command) {
        return execute(command, null);
    }

    @Override
    public ExecuteResult execute(List<String> commands) {
        return execute(commands, null);
    }

    @Override
    public ExecuteResult execute(String command, Callback callback) {
        return execute(command, DEFAULT_CHARSET, callback);
    }

    @Override
    public ExecuteResult execute(List<String> command, Callback callback) {
        return execute(command, DEFAULT_CHARSET, callback, false);

    }

    @Override
    public ExecuteResult execute(String command, String charset, Callback callback) {
        return execute(Collections.singletonList(command), charset, callback, false);
    }

    @Override
    public ExecuteResult execute(List<String> commands, String charset, Callback callback, boolean isAsync) {
        return execute(commands, charset, callback, isAsync, true);
    }

    private ExecuteResult execute(List<String> commands, String charset, Callback callback, boolean isAsync, boolean deleteFlag) {
        ExecuteResult result = new ExecuteResult(0, "", "");
        File file = null;
        try {
            file = File.createTempFile("BAT_SCRIPT_TEMP_" + System.currentTimeMillis(), ".bat");
            String userCmd = String.join("\n", commands);
            FileUtils.write(file, userCmd, charset);

            if (isAsync) {
                File finalFile = file;
                File finalFile1 = file;
                ThreadUtil.FACTORY.submit(() -> {
                    try {
                        exec(Collections.singletonList(finalFile.getCanonicalPath()), charset, callback);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (deleteFlag)
                            finalFile1.deleteOnExit();
                    }
                });
            } else {
                result = exec(Collections.singletonList(file.getCanonicalPath()), charset, callback);
                if (deleteFlag)
                    file.deleteOnExit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null)
                file.deleteOnExit();
        }
        return result;
    }

    private ExecuteResult exec(List<String> commands, String charset, Callback callback) {
        if (commands.isEmpty()) {
            logger.debug("command is empty");
            return new ExecuteResult(0, "command is empty", "");
        }
        String userCmd = String.join(" && ", commands);
        List<String> finalCommands = new ArrayList<>();
        finalCommands.add("cmd");
        finalCommands.add("/c");
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


    @Override
    public ExecuteResult execute(File scriptFile) {
        return execute(scriptFile, null);
    }

    @Override
    public ExecuteResult execute(File scriptFile, Callback callback) {
        return execute(scriptFile, callback, false);
    }

    @Override
    public ExecuteResult execute(File scriptFile, Callback callback, boolean isAsync) {
        try {
            return execute(Collections.singletonList(scriptFile.getCanonicalPath()), DEFAULT_CHARSET, callback, isAsync, false);
        } catch (IOException e) {
            e.printStackTrace();
            return new ExecuteResult(1, e.getMessage(), "");
        }
    }
}
