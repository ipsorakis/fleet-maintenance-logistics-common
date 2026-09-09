package com.fml.common.observability;

import static org.assertj.core.api.Assertions.assertThat;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.testing.exporter.InMemoryMetricReader;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FmlTelemetryTests {

    private static InMemoryMetricReader reader;

    @BeforeAll
    static void registerSdk() {
        GlobalOpenTelemetry.resetForTest();
        reader = InMemoryMetricReader.create();
        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
                .setMeterProvider(SdkMeterProvider.builder().registerMetricReader(reader).build())
                .build();
        GlobalOpenTelemetry.set(sdk);
    }

    @Test
    void instrumentsKeepTheirNames() {
        assertThat(FmlTelemetry.SERVICE_NAME).isEqualTo("fml-monolith");
        assertThat(FmlTelemetry.TELEMETRY_READINGS_INGESTED).isEqualTo("fml.telemetry.readings_ingested");
        assertThat(FmlTelemetry.WORK_ORDERS_CREATED).isEqualTo("fml.maintenance.work_orders_created");
        assertThat(FmlTelemetry.PARTS_REORDERED).isEqualTo("fml.inventory.parts_reordered");
        assertThat(FmlTelemetry.REPORTS_GENERATED).isEqualTo("fml.reporting.reports_generated");
        assertThat(FmlTelemetry.tracer()).isNotNull();
        assertThat(FmlTelemetry.meter()).isNotNull();
    }

    @Test
    void counterMeasurementsAreCollected() {
        FmlTelemetry.workOrdersCreated().add(3);
        FmlTelemetry.telemetryReadingsIngested().add(1);
        FmlTelemetry.partsReordered().add(1);
        FmlTelemetry.reportsGenerated().add(1);

        List<MetricData> metrics = List.copyOf(reader.collectAllMetrics());

        assertThat(metrics)
                .extracting(MetricData::getName)
                .contains(
                        FmlTelemetry.TELEMETRY_READINGS_INGESTED,
                        FmlTelemetry.WORK_ORDERS_CREATED,
                        FmlTelemetry.PARTS_REORDERED,
                        FmlTelemetry.REPORTS_GENERATED);
        assertThat(metrics)
                .allSatisfy(metric ->
                        assertThat(metric.getInstrumentationScopeInfo().getName()).isEqualTo(FmlTelemetry.SERVICE_NAME));

        MetricData workOrders = metrics.stream()
                .filter(metric -> metric.getName().equals(FmlTelemetry.WORK_ORDERS_CREATED))
                .findFirst()
                .orElseThrow();
        assertThat(workOrders.getLongSumData().getPoints())
                .singleElement()
                .satisfies(point -> assertThat(point.getValue()).isEqualTo(3));
    }
}
