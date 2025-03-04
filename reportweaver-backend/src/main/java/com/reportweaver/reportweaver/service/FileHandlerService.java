package com.reportweaver.reportweaver.service;

import org.openqa.selenium.WebDriver;
import com.reportweaver.reportweaver.util.WebScraperUtils;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.reportweaver.reportweaver.websocket.SeleniumStatusHandler;

import com.reportweaver.reportweaver.model.ExtractedData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;

/**
 * Service responsible for handling downloaded files and extracting relevant
 * data.
 * This service opens the downloaded file, extracts key data elements, and
 * returns structured data.
 */
public class FileHandlerService {

    // Logger instance for logging information and errors.
    private static final Logger logger = LoggerFactory.getLogger(FileHandlerService.class);

    // WebDriver instance used for interacting with the file in a browser.
    private final WebDriver driver;

    // Timeout duration for waiting on web elements.
    private final Duration waitTimeout;

    // WebSocket handler for sending real-time status updates to the frontend.
    private final SeleniumStatusHandler seleniumStatusHandler;

    /**
     * Constructs a FileHandlerService with the required dependencies.
     *
     * @param driver                Selenium WebDriver instance for browser
     *                              interactions.
     * @param waitTimeout           Maximum wait time for elements to be located.
     * @param seleniumStatusHandler WebSocket handler for live status updates.
     */
    public FileHandlerService(WebDriver driver, Duration waitTimeout, SeleniumStatusHandler seleniumStatusHandler) {
        this.driver = driver;
        this.waitTimeout = waitTimeout;
        this.seleniumStatusHandler = seleniumStatusHandler;
    }

    /**
     * Handles the downloaded file by opening it in a browser, extracting key
     * information, and returning structured data.
     *
     * @param downloadedFile The file that was downloaded and needs to be processed.
     * @return ExtractedData containing the file's title, first table cell, and
     *         description.
     */
    public ExtractedData handleFile(File downloadedFile) {
        WebDriverWait wait = new WebDriverWait(driver, waitTimeout);

        // Step 1: Convert the file path to a browser-compatible URI and open it
        try {
            seleniumStatusHandler.sendUpdate("Opening downloaded file...");

            // Step 1: Open the file
            String filePath = downloadedFile.getAbsolutePath();
            String fileUri = "file:///" + filePath.replace("\\", "/");
            driver.get(fileUri);

            seleniumStatusHandler.sendUpdate("Opened file: " + fileUri);
            logger.info("Opened file: " + fileUri);

            seleniumStatusHandler.sendUpdate("Extracting title...");
            // Step 2: Extract data using selectors
            String title = WebScraperUtils.getElementText(wait,
                    "body > div > main > div:nth-child(2) > div:nth-child(1) > div > ul > li:nth-child(1) > a > div > div.data-list-item-name");

            seleniumStatusHandler.sendUpdate("Extracting first table cell...");
            String firstTableCell = WebScraperUtils.getElementText(wait,
                    "body > div > main > div:nth-child(2) > div:nth-child(1) > div > ul > li:nth-child(1) > a > div > div.data-list-item-name");

            seleniumStatusHandler.sendUpdate("Extracting description...");
            String description = WebScraperUtils.getElementText(wait,
                    "body > div > main > div:nth-child(2) > div:nth-child(1) > div > ul > li:nth-child(1) > a > div > div.data-list-item-name");

            // Step 3: Return extracted data as an ExtractedData object
            seleniumStatusHandler.sendUpdate("File data extracted successfully.");
            return new ExtractedData(title, firstTableCell, description);

        } catch (Exception e) {
            // Handle any exception that occurs during file processing
            seleniumStatusHandler.sendUpdate("Error handling the file: " + e.getMessage());
            logger.error("Error handling the file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
