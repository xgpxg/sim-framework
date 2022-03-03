package com.yao2san.sim.gateway.route.gray;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrayRoute {
    private String serviceId;
    private String path;
    private String grayVersion;
    private String mainVersion;
    private double weight;
    private RuleType ruleType;
    private Map<String, Object> headers;
    private Map<String, Object> params;
    private Map<String, Object> cookies;

    public enum RuleType {
        WEIGHT_RANDOM,
        PARAM,
        HEADER
    }
}
