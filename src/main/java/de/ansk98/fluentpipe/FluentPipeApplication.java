package de.ansk98.fluentpipe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point of the FluentPipe application.
 *
 * @author ansk98
 */
@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
public class FluentPipeApplication {

    /**
     * Main entry point.
     *
     * @param args command line arguments
     */
    static void main(String[] args) {
        SpringApplication.run(FluentPipeApplication.class, args);
    }

}
