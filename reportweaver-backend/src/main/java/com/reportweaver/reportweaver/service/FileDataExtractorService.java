package com.reportweaver.reportweaver.service;

import com.reportweaver.reportweaver.model.Error;
import com.reportweaver.reportweaver.model.ErrorSummary;

import com.reportweaver.reportweaver.util.WebScraperUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reportweaver.reportweaver.websocket.SeleniumStatusHandler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;

public class FileDataExtractorService {

    // Logger instance for logging important information and errors.
    private static final Logger logger = LoggerFactory.getLogger(FileDataExtractorService.class);

    // WebDriver instance used for interacting with web pages.
    private final WebDriver driver;

    // WebDriverWait instance to handle dynamic waits for web elements.
    private final WebDriverWait wait;

    // Service responsible for handling login operations.
    private final LoginService loginService;

    // WebSocket handler for sending real-time status updates to the frontend.
    private final SeleniumStatusHandler seleniumStatusHandler;

    // Stores the base URL extracted from the report or webpage.
    private String baseURL;

    /**
     * Constructor for FileDataExtractorService.
     *
     * @param driver                Selenium WebDriver instance for interacting with
     *                              the web.
     * @param wait                  WebDriverWait instance for handling wait
     *                              conditions.
     * @param loginService          Service responsible for login operations.
     * @param seleniumStatusHandler WebSocket handler for sending real-time status
     *                              updates.
     */
    public FileDataExtractorService(WebDriver driver, WebDriverWait wait, LoginService loginService,
            SeleniumStatusHandler seleniumStatusHandler) {
        this.driver = driver;
        this.wait = wait;
        this.loginService = loginService;
        this.seleniumStatusHandler = seleniumStatusHandler;
    }

    /**
     * Generates a summary of extracted errors.
     *
     * @param errors List of extracted errors.
     * @return List of ErrorSummary objects containing error names and their total
     *         occurrences.
     */
    public List<ErrorSummary> getErrorSummary(List<Error> errors) {
        List<ErrorSummary> summaryList = new ArrayList<>();
        for (Error error : errors) {
            summaryList.add(new ErrorSummary(error.getErrorName(), error.getTotalErrors()));
        }
        return summaryList;
    }

    /**
     * Extracts data from a specified file by navigating through web elements and
     * retrieving error details.
     *
     * @param filePath The path to the file being processed.
     * @param username The username for authentication if required.
     * @param password The password for authentication if required.
     * @return A list of extracted errors.
     */
    public List<Error> extractData(String filePath, String username, String password) {
        seleniumStatusHandler.sendUpdate("Starting data extraction from file: " + filePath);
        List<Error> errors = new ArrayList<>();

        // Load the file into the browser
        driver.get("file:///" + filePath.replace("\\", "/"));

        // Retrieve all table rows containing report data
        List<WebElement> rows = WebScraperUtils.waitForElements(wait, By.cssSelector(WebScraperUtils.ROW_SELECTOR));

        seleniumStatusHandler.sendUpdate("Number of rows found: " + rows.size());
        logger.info("Number of rows found: {}", rows.size());

        // Extract the base URL from the report
        try {
            WebElement linkElement = WebScraperUtils.waitForElement(wait,
                    By.cssSelector(WebScraperUtils.ERROR_LINK_SELECTOR));
            String fullUrl = linkElement.getAttribute("href");
            seleniumStatusHandler.sendUpdate("Extracting base URL...");
            extractBaseURL(fullUrl);
            seleniumStatusHandler.sendUpdate("Base URL extracted: " + baseURL);
            logger.info("Base URL: " + baseURL);
        } catch (Exception e) {
            seleniumStatusHandler.sendUpdate("Error extracting base URL: " + e.getMessage());
            logger.error("Error extracting base URL: {}", e.getMessage(), e);
            baseURL = ""; // Default to an empty string if parsing fails
        }

        // Process each row in the report to extract relevant error details
        for (WebElement row : rows) {
            processRow(row, errors, username, password);
        }

        // Print extracted errors to the console
        System.out.println("Errors found:");
        errors.forEach(System.out::println);
        seleniumStatusHandler.sendUpdate("Data extraction completed. Errors found: " + errors.size());
        return errors;
    }

    /**
     * Extracts the base URL from a given full URL.
     * This method parses the URL and stores only the protocol (http/https) and
     * host.
     *
     * @param fullUrl The full URL extracted from the document.
     */
    private void extractBaseURL(String fullUrl) {
        try {
            // Parse the URL to extract protocol and host
            URL url = new URL(fullUrl);
            baseURL = url.getProtocol() + "://" + url.getHost();

            // Send update about the extracted base URL
            seleniumStatusHandler.sendUpdate("Base URL extracted: " + baseURL);
            logger.info("Base URL: {}", baseURL);
        } catch (Exception e) {
            // Log the error if URL parsing fails and reset baseURL to an empty string
            logger.error("Error extracting base URL: {}", e.getMessage(), e);
            baseURL = "";
        }
    }

    /**
     * Processes a single row from the extracted report to retrieve error details.
     * This method extracts error-related information, validates it, and fetches
     * additional details if needed.
     *
     * @param row      The WebElement representing a row in the report table.
     * @param errors   A list to store extracted errors.
     * @param username The username used for authentication if required.
     * @param password The password used for authentication if required.
     */
    private void processRow(WebElement row, List<Error> errors, String username, String password) {
        try {
            seleniumStatusHandler.sendUpdate("Processing row...");

            // Extracting error details from the table row
            int instanceCount = Integer.parseInt(WebScraperUtils.getElementText(wait, row, "td:nth-child(4)").trim());
            String errorName = WebScraperUtils.getElementText(wait, row, "th > span > a").trim();
            String categoryText = WebScraperUtils.getElementText(wait, row, "td:nth-child(3)").trim();

            // Validate if the extracted row contains a valid error category
            if (instanceCount > 0 && isValidCategory(categoryText)) {
                seleniumStatusHandler.sendUpdate("Found valid error: " + errorName);

                // Extract documentation and error detail URLs
                String documentationUrl = WebScraperUtils.getAttribute(wait, row, "td:nth-child(1) > a", "href");
                String detailUrl = WebScraperUtils.getAttribute(wait, row, "th > span > a", "href");

                // Fetch error documentation and process details if available
                Error error = fetchErrorDocumentation(documentationUrl, instanceCount, categoryText, errorName);
                if (error != null) {
                    errors.add(error);
                    fetchErrorDetails(detailUrl, error, username, password);
                }
            }
        } catch (Exception e) {
            // Handle exceptions that occur during row processing
            seleniumStatusHandler.sendUpdate("Error processing row: " + e.getMessage());
            logger.info("Error processing row: " + e.getMessage());
        }
    }

    /**
     * Fetches error documentation from a given URL.
     * This method retrieves additional details such as documentation, why the error
     * matters,
     * and how to fix it.
     *
     * @param url           The URL where the error documentation is located.
     * @param instanceCount The number of occurrences of this error.
     * @param categoryText  The category of the error.
     * @param errorName     The name of the error.
     * @return An Error object populated with extracted documentation details, or
     *         null if retrieval fails.
     */
    private Error fetchErrorDocumentation(String url, int instanceCount, String categoryText, String errorName) {
        seleniumStatusHandler.sendUpdate("Fetching error documentation for: " + errorName);
        WebDriver newDriver = null;
        try {
            // Initialize a new WebDriver instance for independent browsing
            newDriver = createDriver();
            WebDriverWait newWait = new WebDriverWait(newDriver, Duration.ofSeconds(10));
            newDriver.get(url);

            // Extract detailed error information from the documentation page
            String errorDocumentation = WebScraperUtils.getElementText(newWait, "#result-documentation-content p");
            String whyItMatters = WebScraperUtils.getElementText(newWait,
                    "#result-documentation-content p:nth-child(2)");
            String howToFixIt = WebScraperUtils.getElementText(newWait, "#result-documentation-content p:nth-child(4)");

            // Return an Error object with extracted details
            seleniumStatusHandler.sendUpdate("Successfully retrieved documentation for: " + errorName);
            return new Error(instanceCount, errorName, categoryText, errorDocumentation, whyItMatters, howToFixIt);
        } catch (Exception e) {
            // Log error if documentation retrieval fails
            seleniumStatusHandler.sendUpdate("Error fetching documentation for " + errorName + ": " + e.getMessage());
            logger.info("Error fetching error documentation: " + e.getMessage());
            return null;
        } finally {
            // Ensure the WebDriver instance is properly closed
            if (newDriver != null) {
                newDriver.quit();
            }
        }
    }

    /**
     * Fetches additional details for a specific error by navigating to its details
     * page.
     * This method logs in if necessary, clicks on the error details link, and
     * processes error count pages.
     *
     * @param url      The URL of the error details page.
     * @param error    The Error object that will be updated with additional
     *                 details.
     * @param username The username for authentication if required.
     * @param password The password for authentication if required.
     */
    private void fetchErrorDetails(String url, Error error, String username, String password) {
        seleniumStatusHandler.sendUpdate("Fetching error details for: " + error.getErrorName());
        WebDriver newDriver = null;

        try {
            // Initialize a new WebDriver instance for independent browsing
            newDriver = createDriver();
            WebDriverWait newWait = new WebDriverWait(newDriver, Duration.ofSeconds(30));
            newDriver.get(url);

            // Perform login if authentication is required
            loginService.performLogin(newDriver, null, username, password);

            // Locate and click the error details link button
            WebElement errorDetailLinkButton = WebScraperUtils.waitForElement(newWait,
                    By.cssSelector(".table > tbody > tr > td:nth-child(7) > a"));
            errorDetailLinkButton.click();

            // Process error details across multiple pages if necessary
            processErrorCountPages(newWait, error);
        } catch (Exception e) {
            // Log error if fetching error details fails
            seleniumStatusHandler
                    .sendUpdate("Error fetching details for " + error.getErrorName() + ": " + e.getMessage());
            logger.error("Error fetching error details: " + e.getMessage());
        } finally {
            // Ensure the WebDriver instance is properly closed
            if (newDriver != null) {
                newDriver.quit();
            }
        }
    }

    /**
     * Processes multiple pages of error counts, extracting data for each error.
     * This method iterates through paginated results and extracts relevant error
     * details.
     *
     * @param wait  WebDriverWait instance for handling dynamic waits.
     * @param error The Error object to which extracted data will be added.
     */
    private void processErrorCountPages(WebDriverWait wait, Error error) {
        while (true) {
            try {
                seleniumStatusHandler.sendUpdate("Processing error count pages for: " + error.getErrorName());

                // Retrieve all rows from the error details table
                List<WebElement> rows = WebScraperUtils.waitForElements(wait,
                        By.cssSelector(WebScraperUtils.ERROR_COUNT_ROW_SELECTOR));

                // Iterate through each row and extract error data
                for (WebElement row : rows) {
                    try {
                        String url = baseURL + WebScraperUtils.getElementText(wait, row, ".column-uri");
                        int count = Integer.parseInt(WebScraperUtils.getElementText(wait, row, ".column-count"));

                        // Log and store extracted error data
                        seleniumStatusHandler.sendUpdate("Extracted error data - URL: " + url + ", Count: " + count);
                        logger.info("URL: " + url);
                        error.addDataEntry(url, count);
                    } catch (TimeoutException e) {
                        // Handle cases where expected elements are not found in a row
                        seleniumStatusHandler.sendUpdate("Cell not found in current row.");
                        logger.error("Cell not found in current row.");
                    }
                }

                // Locate and handle pagination if there are more pages to process
                WebElement nextPageButton = WebScraperUtils.waitForElement(wait,
                        By.cssSelector(".pagination-nextpage"));
                if (!nextPageButton.isEnabled()) {
                    seleniumStatusHandler.sendUpdate("No more pages available for error: " + error.getErrorName());
                    break;
                }

                // Click to navigate to the next page and wait for the new rows to load
                nextPageButton.click();
                wait.until(ExpectedConditions.stalenessOf(rows.get(0)));
            } catch (TimeoutException e) {
                // Handle the case where no more pagination is available
                seleniumStatusHandler.sendUpdate("Pagination ended for: " + error.getErrorName());
                break;
            }
        }
    }

    /**
     * Creates and returns a new WebDriver instance for handling separate browsing
     * sessions.
     * This is useful when multiple instances of a browser are needed for different
     * tasks.
     *
     * @return A new instance of ChromeDriver.
     */
    private WebDriver createDriver() {
        return new ChromeDriver();
    }

    /**
     * Determines if the given error category is valid for processing.
     * This method checks if the category matches predefined valid categories.
     *
     * @param categoryText The category string to validate.
     * @return True if the category is valid, otherwise false.
     */
    private boolean isValidCategory(String categoryText) {
        return "Errors".equalsIgnoreCase(categoryText) || "Contrast Errors".equalsIgnoreCase(categoryText);
    }

}
