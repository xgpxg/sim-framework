package com.yao2san.sim.framework.cloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author wxg
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.cloud.nacos.config")
public class NacosGroup {
    /**
     * groups: configuration groups to be used
     * <p>
     * This group corresponds to the configuration group
     * on nacos and is used for environmental isolation
     */
    private String groups;
}
