package com.fml.common.configuration;

import java.math.BigDecimal;
import java.util.regex.Pattern;
import org.springframework.core.env.Environment;

/**
 * Fallback-preserving {@code boolean}/{@code int}/{@code double}/{@code BigDecimal} reads off a
 * Spring {@link Environment}. Parsing is locale-independent, matching the invariant-culture
 * behaviour of the .NET implementation.
 */
public final class ConfigurationReader {
    private static final Pattern GROUPED_NUMBER =
            Pattern.compile("[+-]?\\d{1,3}(,\\d{3})+(\\.\\d+)?");

    private ConfigurationReader() {
    }

    public static boolean getBool(Environment environment, String key, boolean fallback) {
        return parseBool(environment.getProperty(key), fallback);
    }

    public static int getInt(Environment environment, String key, int fallback) {
        return parseInt(environment.getProperty(key), fallback);
    }

    public static double getDouble(Environment environment, String key, double fallback) {
        return parseDouble(environment.getProperty(key), fallback);
    }

    public static BigDecimal getDecimal(Environment environment, String key, BigDecimal fallback) {
        return parseDecimal(environment.getProperty(key), fallback);
    }

    public static boolean parseBool(String value, boolean fallback) {
        if (value == null) {
            return fallback;
        }
        String trimmed = value.trim();
        if ("true".equalsIgnoreCase(trimmed)) {
            return true;
        }
        if ("false".equalsIgnoreCase(trimmed)) {
            return false;
        }
        return fallback;
    }

    public static int parseInt(String value, int fallback) {
        if (value == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static double parseDouble(String value, double fallback) {
        if (value == null) {
            return fallback;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static BigDecimal parseDecimal(String value, BigDecimal fallback) {
        if (value == null) {
            return fallback;
        }
        String trimmed = value.trim();
        if (GROUPED_NUMBER.matcher(trimmed).matches()) {
            trimmed = trimmed.replace(",", "");
        }
        try {
            return new BigDecimal(trimmed);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
