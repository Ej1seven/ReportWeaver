package com.reportweaver.reportweaver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

/**
 * Configuration class for WebDriver-related settings.
 * This class defines timeout values for Selenium operations.
 */
@Configuration
public class WebDriverConfig {

    /**
     * Defines the standard wait timeout duration.
     * This is typically used for implicit waits in Selenium.
     *
     * @return A Duration of 20 seconds.
     */
    @Bean
    public Duration waitTimeout() {
        return Duration.ofSeconds(20);
    }

    /**
     * Defines an extended wait timeout duration.
     * This can be used for scenarios requiring longer wait times, such as
     * waiting for slow-loading elements.
     *
     * @return A Duration of 120 seconds.
     */
    @Bean
    public Duration extendedWaitTimeout() {
        return Duration.ofSeconds(120);
    }
}
