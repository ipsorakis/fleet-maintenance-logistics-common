using System.Diagnostics.Metrics;
using FML.Common.Observability;

namespace Fml.Common.Tests;

public class FmlTelemetryTests
{
    [Fact]
    public void Instruments_share_the_service_meter_and_keep_their_names()
    {
        Assert.Equal("fml-monolith", FmlTelemetry.ServiceName);
        Assert.Equal(FmlTelemetry.ServiceName, FmlTelemetry.ActivitySource.Name);

        var instruments = new Instrument[]
        {
            FmlTelemetry.TelemetryReadingsIngested,
            FmlTelemetry.WorkOrdersCreated,
            FmlTelemetry.PartsReordered,
            FmlTelemetry.ReportsGenerated,
        };

        Assert.All(instruments, instrument => Assert.Same(FmlTelemetry.Meter, instrument.Meter));
        Assert.Equal(
            new[]
            {
                "fml.telemetry.readings_ingested",
                "fml.maintenance.work_orders_created",
                "fml.inventory.parts_reordered",
                "fml.reporting.reports_generated",
            },
            instruments.Select(i => i.Name));
    }

    [Fact]
    public void Counter_measurements_are_observable_by_a_listener()
    {
        var counter = FmlTelemetry.WorkOrdersCreated;
        long total = 0;
        using var listener = new MeterListener();
        listener.InstrumentPublished = (instrument, l) =>
        {
            if (ReferenceEquals(instrument, counter))
            {
                l.EnableMeasurementEvents(instrument);
            }
        };
        listener.SetMeasurementEventCallback<long>((_, measurement, _, _) => total += measurement);
        listener.Start();

        counter.Add(3);

        Assert.Equal(3, total);
    }
}
