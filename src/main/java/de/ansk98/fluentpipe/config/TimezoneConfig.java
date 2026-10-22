package de.ansk98.fluentpipe.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * Applies the configured timezone as the JVM-wide default on startup.
 *
 * @author ansk98
 */
@Configuration
public class TimezoneConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimezoneConfig.class);

    @Value("${fluentpipe.timezone}")
    private String timezone;

    /**
     * Sets the JVM default timezone so scheduling and all time-based logic share it.
     */
    @PostConstruct
    void applyDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(timezone)));
        LOGGER.info("Default timezone set to {}.", timezone);
    }
}
