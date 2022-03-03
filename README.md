
#### sim-framework

日常积累到的东西，包含一些工具及小框架

基于SpringBoot 2.1.5，集成了大部分web开发常用的依赖、cloud环境以及nacos服务注册发现等服务，可基于此进行快速二次开发。

部分功能截图：

![输入图片说明](images/1.png.png)

![输入图片说明](images/3.png)

#### 如何使用

在pom.xml中添加仓库：

    <repositories>
        <repository>
            <id>rdc-releases</id>
            <url>https://61c59941e74da01affdc9d7b:Swtu1HNF1phz@packages.aliyun.com/maven/repository/2170823-release-TpgdUY/</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>


按需引入依赖：

    <dependency>
      <groupId>com.yao2san</groupId>
      <artifactId>sim-framework-base</artifactId>
      <version>1.0.1</version>
      <type>pom</type>
    </dependency>
  
    <dependency>
      <groupId>com.yao2san</groupId>
      <artifactId>sim-framework-web</artifactId>
      <version>1.0.1</version>
    </dependency>

    <dependency>
      <groupId>com.yao2san</groupId>
      <artifactId>sim-framework-utils</artifactId>
      <version>1.0.1</version>
    </dependency>
    
    <dependency>
      <groupId>com.yao2san</groupId>
      <artifactId>sim-framework-cloud</artifactId>
      <version>1.0.1</version>
    </dependency>
    
#### 启动：

项目根目录下执行：
 
    sh ./bin/build.sh
    sh ./bin/service.sh start all
    


访问地址: http://127.0.0.1:8010

#### 目前可用内容：

1.模拟API:<a href="https://gitee.com/xgpxg/sim-framework/tree/master/sim-api">sim-api</a>

CS模式，客户端sim-api-client扫描所有controller，注册到服务端sim-api-server，服务端进行api管理

支持模拟api的开启/关闭，自定义模拟数据，动态修改模拟数据，新增接口等功能。


2.命令及脚本调用:<a href="https://gitee.com/xgpxg/sim-framework/tree/master/sim-jvoke">sim-jvoke</a>

支持本地/远程执行命令/脚本，简化调用方式。

3.定时任务管理:<a href="https://gitee.com/xgpxg/sim-framework/tree/master/sim-task">sim-task</a>

一个很轻量的定时任务管理。

4.数据库差异对比工具(已支持mysql和oracle):<a href="https://gitee.com/xgpxg/sim-framework/tree/master/sim-db-dif">sim-db-dif</a>

一个数据库差异（表结构、字段等）对比小工具。

5.redis可视化查询:<a href="https://gitee.com/xgpxg/sim-framework/tree/sim-framework-20200621/sim-cache/sim-cache-server">sim-cache-server</a>

一个简单的redis查询工具。封装了大部分redis原生命令，提供Restful的redis连接和查询。

6.vue组件

积累的一些vue组件，持续更新中：
    
- 表格组件sim-el-table-plus：https://www.npmjs.com/package/sim-el-table-plus


------

#### 各模块说明：

- sim-framework-base: 基础依赖

- sim-framework-web: web基础依赖

- sim-framework-utils: 通用工具包

- sim-framework-cloud: cloud环境支持（集成naocs）

- sim-flink:

  - flink-nacos-support: flink集成nacos

- sim-cache:

  - sim-cache-server：redis可视化查询的服务端

- sim-web：web页面集成

- sim-gateway: 网关模块，正在开发中...，基于Zuul，已实现动态路由、动态限流、及灰度路由等功能


----- 




![QQ群](images/2.png.png)

如有疑问，欢迎扫一扫加群哦~

