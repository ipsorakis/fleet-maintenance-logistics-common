using System.Globalization;
using FML.Common.Configuration;
using Microsoft.Extensions.Configuration;

namespace Fml.Common.Tests;

public class ConfigurationReaderTests
{
    private static IConfiguration Configuration(params (string Key, string Value)[] values) =>
        new ConfigurationBuilder()
            .AddInMemoryCollection(values.Select(v => new KeyValuePair<string, string?>(v.Key, v.Value)))
            .Build();

    [Fact]
    public void Values_are_parsed_when_present()
    {
        var configuration = Configuration(
            ("Fml:Flag", "true"),
            ("Fml:Count", "42"),
            ("Fml:Ratio", "1.5"),
            ("Fml:Amount", "250000"));

        Assert.True(configuration.GetBool("Fml:Flag", false));
        Assert.Equal(42, configuration.GetInt("Fml:Count", 0));
        Assert.Equal(1.5, configuration.GetDouble("Fml:Ratio", 0));
        Assert.Equal(250_000m, configuration.GetDecimal("Fml:Amount", 0m));
    }

    [Fact]
    public void Fallbacks_are_used_for_missing_and_unparseable_values()
    {
        var configuration = Configuration(("Fml:Count", "not-a-number"));

        Assert.True(configuration.GetBool("Fml:Missing", true));
        Assert.Equal(7, configuration.GetInt("Fml:Count", 7));
        Assert.Equal(2.0, configuration.GetDouble("Fml:Missing", 2.0));
        Assert.Equal(3m, configuration.GetDecimal("Fml:Missing", 3m));
    }

    [Theory]
    [InlineData("en-US")]
    [InlineData("de-DE")]
    [InlineData("fr-FR")]
    public void Numeric_values_are_parsed_independently_of_the_current_culture(string culture)
    {
        var previous = CultureInfo.CurrentCulture;
        CultureInfo.CurrentCulture = new CultureInfo(culture);
        try
        {
            var configuration = Configuration(
                ("Fml:Ratio", "1.5"),
                ("Fml:Amount", "250000.75"));

            Assert.Equal(1.5, configuration.GetDouble("Fml:Ratio", 0));
            Assert.Equal(250_000.75m, configuration.GetDecimal("Fml:Amount", 0m));
        }
        finally
        {
            CultureInfo.CurrentCulture = previous;
        }
    }
}
