package com.fml.common.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.MapPropertySource;

class ConfigurationReaderTests {

    private static Environment environment(String... keysAndValues) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            values.put(keysAndValues[i], keysAndValues[i + 1]);
        }
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("test", values));
        return environment;
    }

    @Test
    void valuesAreParsedWhenPresent() {
        Environment environment = environment(
                "Fml:Flag", "true",
                "Fml:Count", "42",
                "Fml:Ratio", "1.5",
                "Fml:Amount", "250000");

        assertThat(ConfigurationReader.getBool(environment, "Fml:Flag", false)).isTrue();
        assertThat(ConfigurationReader.getInt(environment, "Fml:Count", 0)).isEqualTo(42);
        assertThat(ConfigurationReader.getDouble(environment, "Fml:Ratio", 0)).isEqualTo(1.5);
        assertThat(ConfigurationReader.getDecimal(environment, "Fml:Amount", BigDecimal.ZERO))
                .isEqualByComparingTo(new BigDecimal("250000"));
    }

    @Test
    void fallbacksAreUsedForMissingAndUnparseableValues() {
        Environment environment = environment("Fml:Count", "not-a-number");

        assertThat(ConfigurationReader.getBool(environment, "Fml:Missing", true)).isTrue();
        assertThat(ConfigurationReader.getBool(environment, "Fml:Count", true)).isTrue();
        assertThat(ConfigurationReader.getInt(environment, "Fml:Count", 7)).isEqualTo(7);
        assertThat(ConfigurationReader.getDouble(environment, "Fml:Missing", 2.0)).isEqualTo(2.0);
        assertThat(ConfigurationReader.getDecimal(environment, "Fml:Missing", new BigDecimal("3")))
                .isEqualByComparingTo(new BigDecimal("3"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"en-US", "de-DE", "fr-FR"})
    void numericValuesAreParsedIndependentlyOfTheCurrentLocale(String locale) {
        Locale previous = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag(locale));
        try {
            Environment environment = environment(
                    "Fml:Ratio", "1.5",
                    "Fml:Amount", "250000.75");

            assertThat(ConfigurationReader.getDouble(environment, "Fml:Ratio", 0)).isEqualTo(1.5);
            assertThat(ConfigurationReader.getDecimal(environment, "Fml:Amount", BigDecimal.ZERO))
                    .isEqualByComparingTo(new BigDecimal("250000.75"));
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    void groupedDecimalsAreParsedLikeTheInvariantCulture() {
        Environment environment = environment("Fml:Amount", "250,000.75");

        assertThat(ConfigurationReader.getDecimal(environment, "Fml:Amount", BigDecimal.ZERO))
                .isEqualByComparingTo(new BigDecimal("250000.75"));
    }
}
