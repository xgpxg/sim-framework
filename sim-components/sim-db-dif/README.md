# db-dif

#### 介绍

这是一个数据库差异对比工具，
可以方便的显示两个数据库中表结构的差异。
在没有部署数据迁移工具的时候，
或者多套环境造成表结构不一致导致程序出问题时，
可以用它来检测表结构的不同。

支持的数据库：

MySQL

Oracle

#### 使用方式

下载源码，使用maven编译打包成jar包，执行命令启动：

```
    java -jar sim-db-dif-1.0.0.jar
```

访问地址：http://localhost:8021

#### 更新日志

2020.03.08：新增oracle对比支持、mysql与oracle缺失或增加对比支持

2020.02.29：修改界面样式，增加菜单；增加对比历史功能；增加对比配置功能；修复一些bug

2020.02.28：新增指定比较字段功能；修复已知bug

#### 示例

连接数据库：

![输入图片说明](https://images.gitee.com/uploads/images/2020/0229/230623_15d6b2a1_1537128.png "30.png")

查看表信息：

![输入图片说明](https://images.gitee.com/uploads/images/2020/0229/231315_428d72f0_1537128.png "34.png")

选择需要比较的表：

![输入图片说明](https://images.gitee.com/uploads/images/2020/0229/230815_823d8d63_1537128.png "31.png")

比较结果：

![输入图片说明](https://images.gitee.com/uploads/images/2020/0229/230925_3323983a_1537128.png "32.png")

详细信息：

![输入图片说明](https://images.gitee.com/uploads/images/2020/0229/231130_08dccbeb_1537128.png "33.png")

查看比较历史：

![输入图片说明](https://images.gitee.com/uploads/images/2020/0229/231559_550e91ee_1537128.png "35.png")

对比设置：

![输入图片说明](https://images.gitee.com/uploads/images/2020/0229/231636_ceaed5e2_1537128.png "36.png")


-----

![输入图片说明](https://images.gitee.com/uploads/images/2020/0308/142358_ca1efc0c_1537128.png "屏幕截图.png")


