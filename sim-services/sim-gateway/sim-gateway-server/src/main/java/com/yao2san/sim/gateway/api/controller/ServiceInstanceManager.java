package com.yao2san.sim.gateway.api.controller;

import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.gateway.api.request.MetadataUpdateReq;
import com.yao2san.sim.gateway.utils.ServiceUtil;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("service-info")
public class ServiceInstanceManager {
    @GetMapping
    public ResponseData list() {
        List<String> services = ServiceUtil.getServices();
        return ResponseData.success(services);
    }

    @GetMapping("inst/{serviceId}")
    public ResponseData serviceInfo(@PathVariable String serviceId) {
        return ResponseData.success(ServiceUtil.getServiceInfo(serviceId));
    }

    @GetMapping("inst")
    public ResponseData instances() {
        Map<String, List<ServiceInstance>> serviceInstanceInfo = ServiceUtil.getServiceInstanceInfo();
        return ResponseData.success(serviceInstanceInfo);
    }

    //TODO
    @GetMapping("inst/{serviceId}/up")
    public ResponseData up(@PathVariable String serviceId) {
        Map<String, List<ServiceInstance>> serviceInstanceInfo = ServiceUtil.getServiceInstanceInfo();
        return ResponseData.success(serviceInstanceInfo);
    }

    //TODO
    @GetMapping("inst/{serviceId}/down")
    public ResponseData down() {
        Map<String, List<ServiceInstance>> serviceInstanceInfo = ServiceUtil.getServiceInstanceInfo();
        return ResponseData.success(serviceInstanceInfo);
    }
    //TODO

    /**
     * update metadata
     */
    @GetMapping("inst/{serviceId}/metadata")
    public ResponseData queryMetadata() {
        Map<String, List<ServiceInstance>> serviceInstanceInfo = ServiceUtil.getServiceInstanceInfo();
        return ResponseData.success(serviceInstanceInfo);
    }

    /**
     * update metadata
     */
    @PostMapping("inst/{serviceId}/metadata")
    public ResponseData updateMetadata(@PathVariable @NotEmpty String serviceId, @RequestBody @Validated MetadataUpdateReq metadataUpdateReq) {
        ServiceUtil.upsertMetadata(
                serviceId,
                metadataUpdateReq.getInstanceId(),
                metadataUpdateReq.getKey(),
                metadataUpdateReq.getValue());
        return ResponseData.success(null, "success");
    }
}
