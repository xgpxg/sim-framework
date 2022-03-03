# sim-task

## 介绍

一个简单的定时任务框架，基于springboot+quartz，对quartz进行再次封装，简化定时任务使用方式，并提供一个web界面对定时任务进行操作以及日志监控。

## 使用方式

### 1. 下载jar包并install到本地maven仓库

[sim-task-client v0.0.1](https://gitee.com/xgpxg/sim-task/releases)

引入maven


```

<dependency>
    <groupId>com.yao2san</groupId>
    <artifactId>sim-task-client</artifactId>
    <version>0.0.1</version>
</dependency>
```


### 2. 在你的Spring项目的启动类上添加注解来开启定时任务功能

    @EnabledSimTask

### 3. 在配置文件中新增配置


```
sim-task:
  # 服务端地址
  server-addr: 127.0.0.1:9000
  # 定时任务要扫描的包
  base-scan-packages: com.yao2san
  # 存储方式(非server将不会注册到服务端)
  store-type: server

```

当你不需要将定时任务注册到服务端进行监控时，只需要配置需要扫描的定时任务的包即可：

```
sim-task:
  base-scan-packages: com.yao2san

```


### 4. 新建定时任务执行类，和使用quartz一样，实现Job接口即可，并使用注解@SimTask来标记它，表明它将被SIM-TASK管理

```
@SimTask(name = "测试任务", cron = "0/5 * * * * ?")
public class TaskTest2 implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            Log.debug(jobExecutionContext, "开始执行");
            Log.info(jobExecutionContext, "执行中...");
            Log.warn(jobExecutionContext, "警告日志");
            Log.info(jobExecutionContext, "任务结束");
            throw new RuntimeException("测试抛出异常");
        } catch (Exception e) {
            e.printStackTrace();
            Log.error(jobExecutionContext, e.getMessage());
        }

    }
}

```

### 5. 持久化日志

提供一个Log工具类将任务的执行日志保存到数据库中（是com.yao2san.simtaskclient.log.Log）：


```
Log.debug(jobExecutionContext, "debug日志");
Log.info(jobExecutionContext, "info日志");
Log.warn(jobExecutionContext, "警告日志");
Log.error(jobExecutionContext, "错误日志");

```

## SIM-TASK服务端

服务端提供定时任务列表查看、暂停、恢复、停止、启动、禁用定时任务的功能，以及执行日志查看。

任务列表：

![输入图片说明](https://images.gitee.com/uploads/images/2020/0105/193546_1774795f_1537128.png "屏幕截图.png")

执行日志：

![输入图片说明](https://images.gitee.com/uploads/images/2020/0105/194509_22be7385_1537128.png "屏幕截图.png")







