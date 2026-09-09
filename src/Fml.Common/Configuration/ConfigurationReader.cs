using Microsoft.Extensions.Configuration;

namespace FML.Common.Configuration;

/// <summary>Fallback-preserving reads of primitive configuration values.</summary>
public static class ConfigurationReader
{
    public static bool GetBool(this IConfiguration configuration, string key, bool fallback) =>
        bool.TryParse(configuration[key], out var parsed) ? parsed : fallback;

    public static int GetInt(this IConfiguration configuration, string key, int fallback) =>
        int.TryParse(configuration[key], out var parsed) ? parsed : fallback;

    public static double GetDouble(this IConfiguration configuration, string key, double fallback) =>
        double.TryParse(configuration[key], out var parsed) ? parsed : fallback;

    public static decimal GetDecimal(this IConfiguration configuration, string key, decimal fallback) =>
        decimal.TryParse(configuration[key], out var parsed) ? parsed : fallback;
}
