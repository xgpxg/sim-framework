package com.yao2san.sim.gateway.api.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MetadataUpdateReq {
    private String instanceId;
    @NotEmpty(message = "metadata key can not be empty")
    private String key;
    private String value;
}
