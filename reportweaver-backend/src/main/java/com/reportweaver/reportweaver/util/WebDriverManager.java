package com.reportweaver.reportweaver.util;

import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages active WebDriver instances in a thread-safe manner.
 * This class handles registering, removing, and stopping WebDriver instances.
 */
@Component
public class WebDriverManager {

    // A synchronized set to store active WebDriver instances, ensuring thread
    // safety.
    private final Set<WebDriver> activeDrivers = Collections.synchronizedSet(new HashSet<>());

    /**
     * Registers a new WebDriver instance by adding it to the active set.
     * 
     * @param driver The WebDriver instance to be added.
     */
    public void addDriver(WebDriver driver) {
        activeDrivers.add(driver);
    }

    /**
     * Removes a WebDriver instance from the active set when it is no longer needed.
     * 
     * @param driver The WebDriver instance to be removed.
     */
    public void removeDriver(WebDriver driver) {
        activeDrivers.remove(driver);
    }

    /**
     * Stops all active WebDriver instances and clears the set.
     * Ensures proper cleanup to avoid memory leaks and lingering browser sessions.
     */
    public void stopAllDrivers() {
        synchronized (activeDrivers) {
            for (WebDriver driver : activeDrivers) {
                try {
                    driver.quit(); // Close the WebDriver session properly.
                } catch (Exception e) {
                    System.err.println("Error closing WebDriver: " + e.getMessage());
                }
            }
            activeDrivers.clear(); // Remove all references to closed WebDrivers.
        }
        System.out.println("All Selenium WebDrivers have been stopped.");
    }
}
