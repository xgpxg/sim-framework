package com.yao2san.sim.gateway.monitor.reporter;

import cn.hutool.core.bean.copier.BeanCopier;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.Timer;
import com.codahale.metrics.*;
import com.yao2san.sim.gateway.config.GatewayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class DatabaseReporter extends ScheduledReporter {

    @Autowired
    private GatewayProperties gatewayProperties;

    private Map<String, DatabaseReport> lastNewReports = new HashMap<>();

    public static DatabaseReporter.Builder forRegistry(MetricRegistry registry) {
        return new DatabaseReporter.Builder(registry);
    }

    public static class Builder {
        private final MetricRegistry registry;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;
        private ScheduledExecutorService executor;
        private boolean shutdownExecutorOnStop;
        private Set<MetricAttribute> disabledMetricAttributes;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
            this.executor = null;
            this.shutdownExecutorOnStop = true;
            disabledMetricAttributes = Collections.emptySet();
        }

        public DatabaseReporter.Builder shutdownExecutorOnStop(boolean shutdownExecutorOnStop) {
            this.shutdownExecutorOnStop = shutdownExecutorOnStop;
            return this;
        }


        public DatabaseReporter.Builder scheduleOn(ScheduledExecutorService executor) {
            this.executor = executor;
            return this;
        }

        public DatabaseReporter.Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }


        public DatabaseReporter.Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }


        public DatabaseReporter.Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }


        public DatabaseReporter.Builder disabledMetricAttributes(Set<MetricAttribute> disabledMetricAttributes) {
            this.disabledMetricAttributes = disabledMetricAttributes;
            return this;
        }


        public DatabaseReporter build() {
            return new DatabaseReporter(registry,
                    rateUnit,
                    durationUnit,
                    filter,
                    executor,
                    shutdownExecutorOnStop,
                    disabledMetricAttributes);
        }
    }

    private DatabaseReporter(MetricRegistry registry,
                             TimeUnit rateUnit,
                             TimeUnit durationUnit,
                             MetricFilter filter,
                             ScheduledExecutorService executor,
                             boolean shutdownExecutorOnStop,
                             Set<MetricAttribute> disabledMetricAttributes) {
        super(registry, "console-reporter", filter, rateUnit, durationUnit, executor, shutdownExecutorOnStop, disabledMetricAttributes);
    }

    @Transactional
    @Override
    @SuppressWarnings("all")
    public void report(SortedMap<String, Gauge> gauges,
                       SortedMap<String, Counter> counters,
                       SortedMap<String, Histogram> histograms,
                       SortedMap<String, Meter> meters,
                       SortedMap<String, Timer> timers) {

        Map<String, DatabaseReport> reportMap = new HashMap<>();

        if (!gauges.isEmpty()) {
            for (Map.Entry<String, Gauge> entry : gauges.entrySet()) {
                //TODO
            }
        }

        if (!counters.isEmpty()) {
            for (Map.Entry<String, Counter> entry : counters.entrySet()) {

                Counter counter = entry.getValue();
                String[] split = entry.getKey().split("\\.");
                String metricObject = split[0];
                String type = split[1];
                String routeId = split[2];

                if (!reportMap.containsKey(routeId)) {
                    reportMap.put(routeId, new DatabaseReport());
                }
                DatabaseReport report = reportMap.get(routeId);

                report.setMetricsName(entry.getKey());
                report.setRouteId(Long.valueOf(routeId));


                if (MetricsType.ALL_COUNT.name().equals(type)) {
                    report.setAllCount(counter.getCount());
                } else if (MetricsType.SUCCESS_COUNT.name().equals(type)) {
                    report.setSuccessCount(counter.getCount());
                } else if (MetricsType.ERROR_COUNT.name().equals(type)) {
                    report.setErrorCount(counter.getCount());
                }
            }
        }

        if (!histograms.isEmpty()) {
            for (Map.Entry<String, Histogram> entry : histograms.entrySet()) {
                //TODO
            }
        }

        if (!meters.isEmpty()) {
            for (Map.Entry<String, Meter> entry : meters.entrySet()) {
                //TODO
            }
        }

        if (!timers.isEmpty()) {
            for (Map.Entry<String, Timer> entry : timers.entrySet()) {

                Timer timer = entry.getValue();

                String[] split = entry.getKey().split("\\.");
                String metricObject = split[0];
                String type = split[1];
                String routeId = split[2];


                if (!reportMap.containsKey(routeId)) {
                    reportMap.put(routeId, new DatabaseReport());
                }
                DatabaseReport report = reportMap.get(routeId);

                report.setMetricsName(entry.getKey());
                report.setMeanRate(timer.getMeanRate());
                report.setM1Rate(timer.getOneMinuteRate());
                report.setM5Rate(timer.getFiveMinuteRate());
                report.setM15Rate(timer.getFifteenMinuteRate());
                report.setMeanTime(timer.getSnapshot().getMean());
                report.setP75LessTime(timer.getSnapshot().get75thPercentile());
                report.setP95LessTime(timer.getSnapshot().get95thPercentile());
                report.setP99LessTime(timer.getSnapshot().get99thPercentile());
                report.setP999LessTime(timer.getSnapshot().get999thPercentile());
                report.setMinTime(timer.getSnapshot().getMin());
                report.setMaxTime(timer.getSnapshot().getMax());
                report.setRouteId(Long.valueOf(routeId));
            }
        }
        if (reportMap.size() > 0) {
            reportMap.forEach((k, v) -> {
                if (lastNewReports.get(k) == null) {
                    lastNewReports.put(k, v);
                } else {

                    DatabaseReport lastNew = lastNewReports.get(k);
                    DatabaseReport temp = BeanCopier.create(v, new DatabaseReport(), new CopyOptions()).copy();
                    v.setAllCount(v.getAllCount() - lastNew.getAllCount());
                    v.setSuccessCount(v.getSuccessCount() - lastNew.getSuccessCount());
                    v.setErrorCount(v.getErrorCount() - lastNew.getErrorCount());
                    lastNewReports.put(k, temp);
                }
                System.out.println(JSONObject.toJSONString(v));
                ReporterHelper.addReport(v);
            });
        }
    }

    @PostConstruct
    private void start() {
        if (gatewayProperties.getReporter().isEnabled()) {
            super.start(gatewayProperties.getReporter().getPeriod(), TimeUnit.SECONDS);
        }
    }

}
