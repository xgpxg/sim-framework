


## sim系列之Jvoke

### 简介

嗯。。。Jvoke（Java-Invoke）简单的封装了一个Java环境下调用系统命令的工具。

[https://gitee.com/xgpxg/sim-jvoke](https://gitee.com/xgpxg/sim-jvoke)

怎么用呢？

拉代码 ， mvn clean packge，打个jar包引入就行了。

要是觉得有用就点个star支持下哦。

### 功能

支持以下调用：

**windows下：**

1.执行windows命令及bat脚本

2.执行shell命令及shell脚本（需安装gti来获取bash环境）

3.执行远程shell命令及脚本

**linux下：**

1.执行本地shell命令及shell脚本

2.执行远程shell命令及shell脚本

### 接口说明
接口定义如下：

```
	ExecuteResult execute(String command);

    ExecuteResult execute(List<String> commands);

    ExecuteResult execute(String command, Callback callback);

    ExecuteResult execute(List<String> commands, Callback callback);

    ExecuteResult execute(String command, String charset, Callback callback);

    ExecuteResult execute(List<String> commands, String charset, Callback callback, boolean isAsync);

    ExecuteResult execute(File scriptFile);

    ExecuteResult execute(File scriptFile, Callback callback);

    ExecuteResult execute(File scriptFile, Callback callback, boolean isAsync);

```

回调：



使用Callback进行回调，用来接受输出内容。Callback定义如下：

```
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

```

 	
### 使用示例

 

```
public class JvokeTest {
    public static void main(String[] args) {
        remoteShell();
    }

    //执行windows命令
    private static void bat() {
        Jvoke jvoke = new BatScript();
        jvoke.execute("dir", new Callback() {
            @Override
            public void output(String line) {
                System.out.println(line);
            }

            @Override
            public void success() {
                System.out.println("command execute success");
            }
        });
    }

    //windows下执行shell命令,命令可以为单条命令，也可以为多条，也可是脚本内容
    private static void gitShell() {
        Jvoke jvoke = new GitShellScript();
        //也可执行脚本
        String content = "#!/bin/bash  \n" +
                "  \n" +
                "for i in $(seq 1 10)  \n" +
                "do   \n" +
                "echo $(expr $i \\* 3 + 1);  \n" +
                "done   \n" +
                "ls\n";
        jvoke.execute("ls", new Callback() {
            @Override
            public void output(String line) {
                System.out.println(line);
            }

            @Override
            public void success() {
                System.out.println("success");
            }
        });
    }

    //linux下执行shell
    private static void shell() {
        Jvoke jvoke = new ShellScript();
        jvoke.execute("ls", new Callback() {
            @Override
            public void output(String line) {
                System.out.println(line);
            }
        });
    }
    //执行远程shell
    private static void remoteShell() {
        Jvoke jvoke = new RemoteShellScript("ip", "username", "password");
        jvoke.execute(new File("~/test.sh"), new Callback() {
            @Override
            public void output(String line) {
                System.out.println(line);
            }
        });
    }
}
```

