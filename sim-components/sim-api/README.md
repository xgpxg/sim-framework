2021/01/01：修修复已知bug

2020/07/25：客户端可指定注册IP

2019/12/10：1.移除mysql依赖，使用H2数据库，使得完全与业务解耦；2.修复已知bug

2019/11/18：1.新增API类型查询条件：自动注册、手动注册；2.增加API列表应用状态显示；3.修复已知bug

2019/11/16：新增手动注册API功能

2019/11/02：1.优化API注册效率；2.修复已知bug

------

# sim-api
#### 介绍

sim-api是一个极简的后端接口数据模拟框架，是为了解决多个服务或系统之间的数据依赖因为开发进度而相互影响的问题，适用于普通SpringBoot项目及微服务。

#### 特点

1. 代码侵入性几乎为0，只需要添加几行配置即可使用

2. 接口信息自动注册、自动同步，无需手动添加接口

3. 简单的操作方式，模拟数据一键开启或关闭

4. 支持不同的请求方式返回不同的模拟数据

#### 原理

1. 通过在应用中引入sim-api-client，扫描controller信息，然后注册到服务端，服务端控制是否开启模拟数据;

2. 客户端缓存服务端模拟接口和数据，拦截应用请求，如果是模拟接口则直接返回模拟数据;

3. 服务端定时扫描客户端信息，检查客户端健康状态。


#### 使用框架

SpringBoot

Vue

Hey ui

#### 稳定版

2019/12：[sim-api-1.0.0](https://gitee.com/xgpxg/sim-api/tree/release%2Fsim-api-1.0.0/)

#### 使用教程

于2019/12/10移除，不再依赖mysql

<del>

准备工作：

sim-api仅需要一张表来持久化api信息，所以需要在你的数据库中创建一张表（Mysql）：

    create table if not exists sa_api
    (
        api_id          int auto_increment comment '主键'
            primary key,
        app_name        varchar(100)                        null comment '应用名称',
        app_addr        varchar(500)                        null comment '应用地址',
        api_name        varchar(100)                        null comment 'api名称',
        api_url         varchar(500)                        null comment 'api地址',
        req_method      varchar(20)                         null comment '请求方法',
        res_data        text                                null comment '响应数据',
        res_type        varchar(200)                        null comment '响应类型',
        api_desc        varchar(500)                        null comment '描述',
        api_type        varchar(10)                         not null default '1000'  comment 'API类型:1000 自动注册,2000 手动注册',
        method          varchar(10000)                      null comment 'api实现方法元数据',
        create_time     timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
        update_time     timestamp on update current_timestamp comment '更新时间',
        open_simulation int       default 0                 null comment '是否开启模拟,0不开启 1开启'
    )
        comment 'api信息';
</del> 

##### 服务端

1. 下载jar包或者获取源码本地编译

2. 修改数据库配置

修改jar包中application.yml配置文件：

    server:
      port: 5221
    spring:
      application:
        name: sim-api-server
      datasource:
        url: 你的数据库连接
        username: 用户名
        password: 密码

3. 执行命令：

    java -jar sim-api-server-0.0.1-SNAPSHOT.jar
    
4. 访问web界面：

    http://localhost:8020
    
 应用列表：
 
![输入图片说明](https://images.gitee.com/uploads/images/2019/0906/130222_31200206_1537128.png "屏幕截图.png")

API列表：

![输入图片说明](https://images.gitee.com/uploads/images/2019/0908/004511_fa43a4e1_1537128.png "屏幕截图.png")

##### 客户端
1. 下载jar包并安装jar包到maven仓库，或者获取源码本地编译也可
2. 引入maven

```
    <dependency>
        <groupId>com.yao2san</groupId>
        <artifactId>sim-api-client</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
```
3. 在项目的yml配置文件中添加配置：

```
    sim-api:
      enabled: true
      server:
        addr: 127.0.0.1:5221
      #2020/07/25新增指定注册的host
      client:
        host: 127.0.0.1
```
        
4. 启动项目，日志输出如下内容，表明接口注册成功：

    start scan api
    
    sim-api : find api [/test/test]
    
    ......
    
    find total 5 api
    
    start register api,server address : http://127.0.0.1:5221/sim-api/register
    
    sim-api registration success

#### 模拟API

1. 开启模拟

在API列表页面，切换“数据模拟”开关来开启/关闭模拟API。

2. 修改模拟数据

在API列表页面，点击“编辑按钮”，修改模拟返回数据。