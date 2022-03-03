package com.yao2san.sim.gateway.monitor.reporter;

public enum MetricsType {
    /**
     * The number of all request
     */
    ALL_COUNT,
    /**
     * The number of response success count(status<400)
     */
    SUCCESS_COUNT,
    /**
     * The number of response fail count(status>=400)
     */
    ERROR_COUNT,
    /**
     * The number of requests whose response time is greater than the set value
     */
    TIMEOUT_COUNT,
    /**
     * Gateway exception, such as authentication failure
     */
    GATEWAY_ERROR_COUNT,

    /**
     * The average time taken from the gateway to send a request to receive a response
     */
    MEAN_TIME,


    TIME_AND_RATE,

    MEAN_RATE,

    M1_RATE,

    M5_RATE,

    M15_RATE,

    P50_TIME,

    P75_TIME,

    P95_TIME,

    P99_TIME,

    P999_TIME

}
