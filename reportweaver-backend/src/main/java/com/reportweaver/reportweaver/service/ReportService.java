package com.reportweaver.reportweaver.service;

import com.reportweaver.reportweaver.model.Error;
import com.reportweaver.reportweaver.util.WebDriverManager;
import com.reportweaver.reportweaver.util.WebScraperUtils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import com.reportweaver.reportweaver.websocket.SeleniumStatusHandler;
import org.springframework.scheduling.annotation.Async;

import io.github.cdimascio.dotenv.Dotenv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service responsible for automating the report generation process.
 * This service logs in to the target website, navigates to the reports section,
 * extracts data, processes rows, generates a Google Docs report, and shares it.
 */
@Lazy
@Service
public class ReportService {

    // Logger instance for logging report generation process activities.
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    // Loads environment variables from the .env file.
    private static final Dotenv dotenv = Dotenv.load();

    // Timeout duration for waiting on web elements.
    private final Duration waitTimeout;

    // Service responsible for handling login operations.
    private final LoginService loginService;

    // Service responsible for processing report rows.
    private final RowProcessorService rowProcessorService;

    // Service for creating and managing Google Docs reports.
    private final GoogleDocsService googleDocsService;

    // WebSocket handler for sending real-time status updates.
    private final SeleniumStatusHandler seleniumStatusHandler;

    // Manages Selenium WebDriver instances.
    private final WebDriverManager seleniumManager;

    /**
     * Constructs a ReportService with the required dependencies.
     *
     * @param waitTimeout           Timeout duration for Selenium WebDriver waits.
     * @param loginService          Service for handling authentication.
     * @param rowProcessorService   Service for processing extracted report rows.
     * @param googleDocsService     Service for managing Google Docs reports.
     * @param seleniumStatusHandler WebSocket handler for real-time status updates.
     * @param seleniumManager       Manages multiple Selenium WebDriver instances.
     */
    public ReportService(Duration waitTimeout, LoginService loginService,
            RowProcessorService rowProcessorService, GoogleDocsService googleDocsService,
            SeleniumStatusHandler seleniumStatusHandler, WebDriverManager seleniumManager) {
        this.waitTimeout = waitTimeout;
        this.loginService = loginService;
        this.rowProcessorService = rowProcessorService;
        this.googleDocsService = googleDocsService;
        this.seleniumStatusHandler = seleniumStatusHandler;
        this.seleniumManager = seleniumManager;
    }

    /**
     * Asynchronously runs the report generation process.
     * This method handles authentication, report data extraction, processing, and
     * report creation.
     *
     * @param website  The target website URL.
     * @param username The username for login authentication.
     * @param password The password for login authentication.
     * @param email    The email address where the generated report should be
     *                 shared.
     * @return A CompletableFuture containing the generated Google Docs report ID.
     */
    @Async
    public CompletableFuture<String> runReportProcess(String website, String username, String password, String email) {
        // Initialize WebDriver with browser options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        WebDriver driver = new ChromeDriver(options);
        seleniumManager.addDriver(driver);
        seleniumStatusHandler.sendUpdate("Selenium WebDriver initialized.");

        WebDriverWait wait = new WebDriverWait(driver, waitTimeout);
        String documentId = "";

        try {
            // Retrieve the Pope Tech login URL from environment variables
            String popeTechUrl = dotenv.get("POPE_TECH_URL");
            if (popeTechUrl == null || popeTechUrl.isEmpty()) {
                throw new IllegalStateException("POPE_TECH_URL environment variable is missing.");
            }

            seleniumStatusHandler.sendUpdate("Performing login...");
            loginService.performLogin(driver, popeTechUrl, username, password);

            seleniumStatusHandler.sendUpdate("Navigating to reports...");
            WebScraperUtils.navigateToReports(wait, driver);

            seleniumStatusHandler.sendUpdate("Fetching report rows...");
            List<WebElement> rows = WebScraperUtils.getReportRows(wait, driver);
            logger.info("Number of rows found: {}", rows.size());

            seleniumStatusHandler.sendUpdate("Processing report rows...");
            String downloadedFilePath = rowProcessorService.processRows(website, rows, driver, wait);

            if (downloadedFilePath != null) {
                logger.info("File downloaded at: {}", downloadedFilePath);

                // Extract errors from the downloaded report file
                seleniumStatusHandler.sendUpdate("Extracting data from downloaded report...");
                FileDataExtractorService extractorService = new FileDataExtractorService(driver, wait, loginService,
                        seleniumStatusHandler);
                List<Error> errors = extractorService.extractData(downloadedFilePath, username, password);
                logger.info("Errors passed to report: {}", errors);

                // Generate a Google Docs report with extracted errors
                seleniumStatusHandler.sendUpdate("Generating Google Doc with extracted errors...");
                CompletableFuture<String> docFuture = googleDocsService.createAccessibilityReport("Error Report",
                        errors, extractorService);
                documentId = docFuture.get();

                // Share the generated report via email
                seleniumStatusHandler.sendUpdate("Sharing Google Doc...");
                googleDocsService.shareDocument(documentId, email);

                seleniumStatusHandler.sendUpdate("Google Doc created and shared successfully!");
                logger.info("Google Doc created and shared successfully!");
            } else {
                seleniumStatusHandler.sendUpdate("No file was downloaded. Report may be empty.");
                logger.error("No file was downloaded.");
            }

            seleniumStatusHandler.sendUpdate("Report process completed successfully!");
            logger.info("Report process completed successfully!");

        } catch (Exception e) {
            // Handle exceptions that occur during the report process
            seleniumStatusHandler.sendUpdate("Error during report process: " + e.getMessage());
            logger.error("An error occurred during the report process: {}", e.getMessage(), e);
        } finally {
            // Close WebDriver after execution
            seleniumStatusHandler.sendUpdate("Closing WebDriver...");
            seleniumManager.removeDriver(driver);
            driver.quit();
        }

        // Ensure a valid response is returned if the document ID is empty
        if (documentId == null || documentId.isEmpty()) {
            seleniumStatusHandler.sendUpdate("Google Doc ID is empty. Please try again");
            logger.error("Document ID is empty. Returning 'Processing'.");
            return CompletableFuture.completedFuture("Processing");
        }

        return CompletableFuture.completedFuture(documentId);
    }
}
