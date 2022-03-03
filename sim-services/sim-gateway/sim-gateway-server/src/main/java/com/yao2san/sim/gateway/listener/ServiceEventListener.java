/*
package com.yao2san.sim.gateway.listener;

import com.netflix.appinfo.InstanceInfo;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ServiceEventListener {
    @EventListener(classes = {EurekaInstanceRegisteredEvent.class})
    public void listen(EurekaInstanceRegisteredEvent event) {
        InstanceInfo instanceInfo = event.getInstanceInfo();
        System.out.println("服务注册：" + instanceInfo);
    }

    @EventListener(classes = {EurekaInstanceCanceledEvent.class})
    public void listen(EurekaInstanceCanceledEvent event) {
        String appName = event.getAppName();
        String serverId = event.getServerId();
        System.out.println("服务下线：" + appName);
    }
}
*/
