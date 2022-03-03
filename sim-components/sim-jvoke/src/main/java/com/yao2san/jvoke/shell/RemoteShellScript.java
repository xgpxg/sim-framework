package com.yao2san.jvoke.shell;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.yao2san.jvoke.base.Callback;
import com.yao2san.jvoke.base.ExecuteResult;
import com.yao2san.jvoke.base.Jvoke;
import com.yao2san.jvoke.common.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

/**
 * 执行远程Shell命令及远程脚本
 */
public class RemoteShellScript implements Jvoke {
    private static final Logger logger = LoggerFactory.getLogger(RemoteShellScript.class);
    private static final String DEFAULT_CHARSET = "gbk";
    private static final int DEFAULT_SSH_PORT = 22;
    private String host;

    private String username;

    private String password;

    public RemoteShellScript(final String host, final String username, final String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

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
                execRemote(String.join(" && ", commands), charset, callback);
            });
        } else {
            return execRemote(String.join(" && ", commands), charset, callback);
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
        if (isAsync) {
            ThreadUtil.FACTORY.submit(() -> {
                try {
                    execRemote("sh " + scriptFile.getCanonicalPath(), DEFAULT_CHARSET, callback);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            if(System.getProperty("os.name").toLowerCase().contains("windows"))
                return execRemote("sh " + (scriptFile.getParent() + "/" + scriptFile.getName()).replace("\\", "/"), DEFAULT_CHARSET, callback);
            else {
                try {
                    return execRemote("sh " + scriptFile.getCanonicalPath(), DEFAULT_CHARSET, callback);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new ExecuteResult(0, "", "");
    }

    private ExecuteResult execRemote(String command, String charset, Callback callback) {
        JSch jsch = new JSch();

        try {
            // Create and connect session.
            Session session = jsch.getSession(username, host, DEFAULT_SSH_PORT);
            session.setPassword(password);
            //session.setUserInfo(userInfo);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(30000);

            // Create and connect channel.
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            channel.setInputStream(null);
            BufferedReader input = new BufferedReader(new InputStreamReader(channel
                    .getInputStream()));
            BufferedReader errorInput = new BufferedReader(new InputStreamReader(((ChannelExec) channel)
                    .getErrStream()));

            channel.connect();
            logger.info("The remote command is: {}", command);

            // Get the output of remote command.
            ExecuteResult result = new ExecuteResult();
            String line;
            StringBuilder data = new StringBuilder();
            while ((line = input.readLine()) != null) {
                data.append(line);
                if (callback != null) {
                    callback.output(line);
                }
            }
            while ((line = errorInput.readLine()) != null) {
                data.append(line);
                if (callback != null) {
                    callback.output(line);
                }
            }
            input.close();
            errorInput.close();
            // Get the return code only after the channel is closed.
            if (channel.isClosed()) {
                result.setCode(channel.getExitStatus());
                result.setData(data.toString());
            }

            // Disconnect the channel and session.
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ExecuteResult(1, "", "");
    }
}
