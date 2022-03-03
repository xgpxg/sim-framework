package com.yao2san.sim.gateway.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Data
@Configuration
@ConfigurationProperties(prefix = "zuul")
public class GatewayProperties {


    private ReporterProperties reporter;

    private GrayRouteProperties grayRoute;

    private DynamicRoute dynamicRoute;

    private RegistryType registryType;

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "reporter")
    @NoArgsConstructor
    public static class ReporterProperties {
        /**
         * How often do you record data
         */
        private Integer period;

        private Long retentionTime;

        private TimeUnit retentionTimeUnit;

        private String cleanOpportunity;

        private boolean enabled = false;

    }

    @Data
    @NoArgsConstructor
    @Configuration
    @ConfigurationProperties(prefix = "gray-route")
    public static class GrayRouteProperties {
        private boolean enable = false;

        /**
         * key is service id.
         */
        private Map<String, GrayRoute> routes = new LinkedHashMap<>();


    }

    @Data
    @NoArgsConstructor
    public static class GrayRoute {
        private String path;
        private String grayVersion;
        private String mainVersion;
        private double weight;
        private com.yao2san.sim.gateway.route.gray.GrayRoute.RuleType type;
        private Map<String, Object> headers;
        private Map<String, Object> params;
        private Map<String, Object> cookies;

        private Set<String> serviceInstances;

    }

    @Data
    @NoArgsConstructor
    private static class DynamicRoute {
        private int refreshIntervalSeconds = 30;
    }

    public enum RegistryType {
        /**
         * eureka
         */
        EUREKA,
        /**
         * nacos
         */
        NACOS
    }
}
