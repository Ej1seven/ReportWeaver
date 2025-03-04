package com.reportweaver.reportweaver.service;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import com.reportweaver.reportweaver.util.DownloadUtil;
import com.reportweaver.reportweaver.websocket.SeleniumStatusHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

@Lazy
@Service
public class RowProcessorService {

    // Logger instance for logging debug and error messages
    private static final Logger logger = LoggerFactory.getLogger(RowProcessorService.class);

    // WebSocket handler for sending status updates during processing
    private final SeleniumStatusHandler seleniumStatusHandler;

    // Constructor to initialize the SeleniumStatusHandler
    public RowProcessorService(SeleniumStatusHandler seleniumStatusHandler) {
        this.seleniumStatusHandler = seleniumStatusHandler;
    }

    /**
     * Processes rows from a given table in the webpage, searching for a row
     * that matches the provided website and has an HTML file format.
     *
     * @param website The target website to match in the "column-entities" cell.
     * @param rows    List of WebElements representing table rows.
     * @param driver  The Selenium WebDriver instance.
     * @param wait    WebDriverWait instance for waiting on elements.
     * @return The file path of the downloaded file, or null if no matching row is
     *         found.
     */
    public String processRows(String website, List<WebElement> rows, WebDriver driver, WebDriverWait wait) {
        seleniumStatusHandler.sendUpdate("Starting row processing...");

        // Loop continuously until a matching row is found or pagination ends
        while (true) {
            for (WebElement row : rows) {
                try {
                    seleniumStatusHandler.sendUpdate("Checking row for matching entity...");

                    // Locate the "column-entities" cell first
                    WebElement entitiesCell = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(
                            row, By.className("column-entities")));

                    // Check if the entity matches first
                    if (!entitiesCell.getText().contains(website)) {
                        continue; // Skip this row if the entity doesn't match
                    }

                    // Locate the "column-format" cell only if the entity matches
                    WebElement fileFormatCell = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(
                            row, By.className("column-format")));

                    // Check if file format is "html"
                    if (!"html".equalsIgnoreCase(fileFormatCell.getText())) {
                        continue; // Skip this row if file format is not "html"
                    }

                    // Locate all elements with class "data-column ng-star-inserted" for scan type
                    // check
                    List<WebElement> scanTypeCells = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("ng-star-inserted")));

                    // Check if any of these elements contain the text "Website"
                    boolean isWebsiteScanType = scanTypeCells.stream()
                            .anyMatch(cell -> cell.getText().trim().equalsIgnoreCase("Website"));

                    // If all conditions are met, proceed with downloading
                    if (isWebsiteScanType) {
                        seleniumStatusHandler.sendUpdate("Matching row found: " + row.getText());
                        logger.info("Row found: " + row.getText());

                        // Locate and click the download button within the matching row
                        WebElement downloadButton = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(
                                row, By.cssSelector("button:has(.fa-download)")));

                        seleniumStatusHandler.sendUpdate("Clicking download button...");

                        // Handle the file download process
                        File downloadedFile = DownloadUtil.handleFileDownload(downloadButton, 30);

                        if (downloadedFile != null) {
                            seleniumStatusHandler
                                    .sendUpdate("File downloaded successfully: " + downloadedFile.getAbsolutePath());
                            logger.info("File downloaded successfully: " + downloadedFile.getAbsolutePath());
                            return downloadedFile.getAbsolutePath();
                        } else {
                            seleniumStatusHandler.sendUpdate("File download failed or timed out.");
                            logger.error("File download failed or timed out.");
                            return null;
                        }
                    }
                } catch (TimeoutException e) {
                    // Handle cases where the expected cells are not found within a row
                    seleniumStatusHandler.sendUpdate("Cell not found in current row, retrying...");
                    logger.error("Cell not found in current row, retrying...");
                }
            }

            // Handle pagination - navigate to the next page if available
            try {
                seleniumStatusHandler.sendUpdate("Checking for next page...");

                // Locate the "Next Page" button
                WebElement nextPageButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#reports-table > data-table > div > data-table-pagination > div:nth-child(2) > div.pagination-pages.offset-md-3.col-md-6 > div > div > button.btn.btn-default.pagination-nextpage")));

                // Click the "Next Page" button if it's enabled
                if (nextPageButton.isEnabled()) {
                    seleniumStatusHandler.sendUpdate("Navigating to next page...");
                    nextPageButton.click();

                    // Wait for the current rows to be replaced with new data
                    wait.until(ExpectedConditions.stalenessOf(rows.get(0)));

                    // Refresh the list of rows after page navigation
                    rows = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                            By.cssSelector("#reports-table > data-table > div > div tbody tr")));
                } else {
                    seleniumStatusHandler.sendUpdate("No more pages available.");
                    logger.info("No more pages available.");
                    break; // Exit the loop if no further pages are available
                }
            } catch (TimeoutException e) {
                seleniumStatusHandler.sendUpdate("Next page button not found or not clickable. Ending pagination.");
                logger.error("Next page button not found or not clickable.");
                break; // Exit pagination loop
            }
        }

        // Log and notify that the desired row was not found after all pages were
        // processed
        seleniumStatusHandler.sendUpdate("Desired row not found after processing all pages.");
        logger.error("Desired row not found after processing all pages.");
        return null;
    }
}
