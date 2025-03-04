package com.reportweaver.reportweaver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.reportweaver.reportweaver.util.WebDriverManager;

/**
 * REST controller for managing Selenium WebDriver sessions.
 * Provides endpoints to control and manage Selenium processes.
 */
@RestController
@RequestMapping("/api/server")
@CrossOrigin(origins = "http://localhost:5173") // Allows frontend requests from localhost:5173
public class ServerController {

    private final WebDriverManager seleniumManager;

    /**
     * Constructor-based dependency injection for SeleniumManager.
     *
     * @param seleniumManager Service responsible for managing Selenium WebDriver
     *                        sessions.
     */
    public ServerController(WebDriverManager seleniumManager) {
        this.seleniumManager = seleniumManager;
    }

    /**
     * Stops all active Selenium WebDriver sessions.
     * This endpoint is used to terminate all WebDriver instances managed by
     * SeleniumManager.
     *
     * @return ResponseEntity with a success message or an error message in case of
     *         failure.
     */
    @PostMapping("/stop-selenium")
    public ResponseEntity<String> stopSeleniumSessions() {
        seleniumManager.stopAllDrivers();

        // ✅ Introduce a delay to ensure all WebDrivers have fully closed before sending
        // a response.
        try {
            Thread.sleep(3000); // Wait 3 seconds before responding
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupt status
            return ResponseEntity.internalServerError().body("Error while stopping Selenium WebDrivers.");
        }

        return ResponseEntity.ok("All active Selenium WebDriver sessions have been stopped.");
    }
}
