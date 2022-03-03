package com.yao2san.sim.gateway.monitor.reporter;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class DatabaseReport {

    private Long routeMetricsId = 0L;

    private Long routeId;

    private String metricsName;

    private Long allCount = 0L;

    private Long successCount = 0L;

    private Long errorCount = 0L;

    private Long timeoutCount = 0L;

    private Long gatewayErrorCount = 0L;

    private Double timeAndRate = 0.0;

    private Double meanRate = 0.0;

    private Double m1Rate = 0.0;

    private Double m5Rate = 0.0;

    private Double m15Rate = 0.0;

    private Long minTime = 0L;

    private Long maxTime = 0L;

    private Double meanTime = 0.0;

    private Double p75LessTime = 0.0;

    private Double p95LessTime = 0.0;

    private Double p99LessTime = 0.0;

    private Double p999LessTime = 0.0;
}
