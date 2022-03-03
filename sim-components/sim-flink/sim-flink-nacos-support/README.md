##### Flink集成Nacos

###### 使用方式 

clone项目,maven install后，引入pom即可

```xml
     <dependency>
        <groupId>com.yao2san</groupId>
        <artifactId>sim-flink-nacos-support</artifactId>
        <version>1.0.0</version>
     </dependency>
```

###### 配置
在项目resource下新建：application.properties

```properties
# 是否开启nacos支持
sim.flink.nacos.enable=true
# nacos地址
sim.flink.nacos.serverAddr=
# nacos命名空间
sim.flink.nacos.namespace=
# nacos配置分组 
sim.flink.nacos.groups=
# nacos的data-id
sim.flink.nacos.dataIds=
```

```java

String configValue = FlinkNacosConfig.getConfigValue(key);

```