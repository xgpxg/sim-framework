### redis可视化

一个简单的redis可视化查询工具。

- 支持的数据类型：string、list、hash、set、zset

- 支持查询结果展示格式化为json展示

- 支持单机和集群redis

- 支持大部分redis原生命令，通过web端命令行操作

![输入图片说明](https://images.gitee.com/uploads/images/2021/0314/133206_5c900bc9_1537128.png "屏幕截图.png")

![输入图片说明](https://images.gitee.com/uploads/images/2021/0313/163826_14d055a6_1537128.png "屏幕截图.png")


![输入图片说明](https://images.gitee.com/uploads/images/2021/0313/163949_ae63fd9f_1537128.png "屏幕截图.png")

### 工程结构
    
    |-- sim-cache
    
         |-- sim-cache-server 服务端
         
    |-- sim-web
    
         |-- src
         
             |-- main
             
                |-- webapp
                
                    |-- src
                
                        |-- views
                
                            |-- service
                
                                |-- utils
                
                                    |-- redis-query 页面
         
     


### 实现原理

可视化查询：基于RedisTemplate

命令行：基于RedisTemplate执行lua脚本。对命令进行了解析，提取出key和arg，封装为lua脚本。


### 如何运行

- git拉取代码

- 代码根目录执行

    maven install -Dmaven.test.skip=true

- 进入目录sim-cache/sim-cache-server打包启动

- 进入sim-web/src/main/webapp目录，执行

    cnpm install

    cnpm run serve

- 浏览器访问：http://localhost:9527/#/util/redis-query
