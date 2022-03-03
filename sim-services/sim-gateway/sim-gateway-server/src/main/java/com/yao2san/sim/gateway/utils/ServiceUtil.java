package com.yao2san.sim.gateway.utils;

import com.yao2san.sim.framework.web.exception.BusiException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ServiceUtil {

    private static DiscoveryClient discoveryClient;

    @Autowired
    public void setDiscoveryClient(DiscoveryClient discoveryClient) {
        ServiceUtil.discoveryClient = discoveryClient;
    }

    public static List<String> getServices() {
        return discoveryClient.getServices();
    }

    public static Map<String, List<ServiceInstance>> getServiceInstanceInfo() {
        Map<String, List<ServiceInstance>> serviceInstances = new HashMap<>();
        List<String> serviceNames = discoveryClient.getServices();
        for (String serviceName : serviceNames) {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
            serviceInstances.put(serviceName, instances);
        }
        return serviceInstances;
    }

    public static ServiceInstanceInfo getServiceInfo(String targetServiceName) {
        ServiceInstanceInfo serviceInstances = new ServiceInstanceInfo();
        List<String> serviceNames = discoveryClient.getServices();
        for (String serviceName : serviceNames) {
            if (serviceName.equals(targetServiceName)) {
                List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
                serviceInstances.name = serviceName;
                serviceInstances.count = instances.size();
                serviceInstances.instances = instances;
                break;
            }
        }
        return serviceInstances;
    }

    @SuppressWarnings("all")
    public static void upsertMetadata(String serviceId, String key, String value) {
        upsertMetadata(serviceId, null, key, value);
    }
    @SuppressWarnings("all")
    public static void upsertMetadata(String serviceId, String instanceId, String key, String value) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        for (ServiceInstance instance : instances) {
            if (instanceId == null || "".equals(instanceId)) {
                instance.getMetadata().put(key, value);
            } else {
                if (instance.getInstanceId().equals(instanceId)) {
                    instance.getMetadata().put(key, value);
                    return;
                }
            }
        }
        if (instanceId != null && !"".equals(instanceId)) {
            log.error("no instance found,serviceId:" + serviceId + ",instanceId:" + instanceId);
        }
    }

    public static void deleteMetadata(String serviceId, String key) {
        deleteMetadata(serviceId, null, key);
    }

    public static void deleteMetadata(String serviceId, String instanceId, String key) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        for (ServiceInstance instance : instances) {
            if (instanceId == null || "".equals(instanceId)) {
                instance.getMetadata().remove(key);
            } else {
                if (instance.getInstanceId().equals(instanceId)) {
                    instance.getMetadata().remove(key);
                    return;
                }
            }
        }
        if (instanceId != null && !"".equals(instanceId)) {
            throw new BusiException("no instance found,serviceId:" + serviceId + ",instanceId:" + instanceId);
        }
    }

    //TODO
    public static boolean isAvailable() {
        return false;
    }

    @Data
    static class ServiceInstanceInfo {
        String name;
        int count;
        List<ServiceInstance> instances;
    }
}
