package com.fml.common.observability;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;

/** OpenTelemetry tracer and meter shared by all FML modules. */
public final class FmlTelemetry {
    public static final String SERVICE_NAME = "fml-monolith";

    public static final String TELEMETRY_READINGS_INGESTED = "fml.telemetry.readings_ingested";
    public static final String WORK_ORDERS_CREATED = "fml.maintenance.work_orders_created";
    public static final String PARTS_REORDERED = "fml.inventory.parts_reordered";
    public static final String REPORTS_GENERATED = "fml.reporting.reports_generated";

    private FmlTelemetry() {
    }

    public static Tracer tracer() {
        return Instruments.get().tracer;
    }

    public static Meter meter() {
        return Instruments.get().meter;
    }

    public static LongCounter telemetryReadingsIngested() {
        return Instruments.get().telemetryReadingsIngested;
    }

    public static LongCounter workOrdersCreated() {
        return Instruments.get().workOrdersCreated;
    }

    public static LongCounter partsReordered() {
        return Instruments.get().partsReordered;
    }

    public static LongCounter reportsGenerated() {
        return Instruments.get().reportsGenerated;
    }

    private static final class Instruments {
        private static volatile Instruments instance;

        private final Tracer tracer;
        private final Meter meter;
        private final LongCounter telemetryReadingsIngested;
        private final LongCounter workOrdersCreated;
        private final LongCounter partsReordered;
        private final LongCounter reportsGenerated;

        private Instruments(OpenTelemetry openTelemetry) {
            this.tracer = openTelemetry.getTracer(SERVICE_NAME);
            this.meter = openTelemetry.getMeter(SERVICE_NAME);
            this.telemetryReadingsIngested = meter.counterBuilder(TELEMETRY_READINGS_INGESTED).build();
            this.workOrdersCreated = meter.counterBuilder(WORK_ORDERS_CREATED).build();
            this.partsReordered = meter.counterBuilder(PARTS_REORDERED).build();
            this.reportsGenerated = meter.counterBuilder(REPORTS_GENERATED).build();
        }

        private static Instruments get() {
            Instruments local = instance;
            if (local == null) {
                synchronized (Instruments.class) {
                    local = instance;
                    if (local == null) {
                        local = new Instruments(GlobalOpenTelemetry.get());
                        instance = local;
                    }
                }
            }
            return local;
        }
    }
}
