package com.reportweaver.reportweaver.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.reportweaver.reportweaver.websocket.SeleniumStatusHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;

public class DownloadUtil {

    private static final Logger logger = LoggerFactory.getLogger(DownloadUtil.class);
    private static final String DOWNLOAD_FOLDER = System.getProperty("user.home") + "/Downloads";
    private static final SeleniumStatusHandler seleniumStatusHandler = new SeleniumStatusHandler();

    /**
     * Gets the default download folder path.
     *
     * @return the download folder path as a String.
     */
    public static String getDownloadFolder() {
        return DOWNLOAD_FOLDER;
    }

    public Duration waitTimeout() {
        return Duration.ofSeconds(20);
    }

    /**
     * Waits for a file to be downloaded after clicking the provided button.
     *
     * @param downloadButton the WebElement representing the download button.
     * @param timeoutSeconds the timeout in seconds to wait for the download.
     * @return the downloaded File, or null if the download times out.
     */
    public static File handleFileDownload(WebElement downloadButton, int timeoutSeconds) {
        seleniumStatusHandler.sendUpdate("Clicking download button...");
        // Track the time before clicking the button
        long clickTime = System.currentTimeMillis();

        // Click the download button
        downloadButton.click();

        // Log and wait for the file download
        seleniumStatusHandler.sendUpdate("Waiting for file download...");
        logger.info("Waiting for file download...");
        File downloadedFile = waitForFileDownload(DOWNLOAD_FOLDER, timeoutSeconds, clickTime);

        if (downloadedFile != null) {
            seleniumStatusHandler.sendUpdate("Downloaded file detected: " + downloadedFile.getName());
            logger.info("Downloaded file: " + downloadedFile.getName());
        } else {
            seleniumStatusHandler.sendUpdate("Download timed out or file not found.");
            logger.error("Download timed out or file not found.");
        }

        return downloadedFile;
    }

    /**
     * Waits for a file to appear in the specified directory.
     *
     * @param downloadFolder the folder where files are expected to be downloaded.
     * @param timeoutSeconds the timeout in seconds to wait for the file.
     * @return the downloaded File, or null if no file is found within the timeout.
     */
    public static File waitForFileDownload(String downloadFolder, int timeoutSeconds, long clickTime) {
        seleniumStatusHandler.sendUpdate("Monitoring download folder: " + downloadFolder);
        File folder = new File(downloadFolder);
        long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000);

        File newestFile = null;

        while (System.currentTimeMillis() < endTime) {
            File[] files = folder.listFiles(file -> file.isFile() && !file.getName().equals(".DS_Store")
                    && file.lastModified() > clickTime); // Ensure the file is created after click

            if (files != null && files.length > 0) {
                // Find the newest file based on lastModified timestamp
                newestFile = Arrays.stream(files)
                        .max(Comparator.comparingLong(File::lastModified))
                        .orElse(null);

                if (newestFile != null
                        && isFileComplete(newestFile)
                        && newestFile.getName().matches(".*\\.(pdf|xlsx|txt|csv|html)$")) { // Expected extensions
                    seleniumStatusHandler.sendUpdate("File downloaded successfully: " + newestFile.getName());
                    return newestFile;
                }
            }

            try {
                Thread.sleep(1000); // Wait 500ms before re-checking
            } catch (InterruptedException e) {
                seleniumStatusHandler.sendUpdate("Thread interrupted while waiting for file download.");
                Thread.currentThread().interrupt();
                logger.error("Thread interrupted while waiting for file download.");
                break;
            }
        }
        seleniumStatusHandler.sendUpdate("Download timeout reached. No valid file detected.");
        return null; // Return null if timeout is reached
    }

    /**
     * Checks if the file is completely downloaded by verifying its size remains
     * stable.
     *
     * @param file the file to check.
     * @return true if the file is complete, false otherwise.
     */
    private static boolean isFileComplete(File file) {
        if (file.getName().endsWith(".crdownload")) {
            seleniumStatusHandler.sendUpdate("File is still downloading: " + file.getName());
            return false; // File is still being downloaded if it has .crdownload extension
        }

        long initialSize = file.length();
        try {
            Thread.sleep(2000); // Wait for a second to check if the file size changes
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            seleniumStatusHandler.sendUpdate("Thread interrupted during file completeness check.");
            logger.error("Thread interrupted during file completeness check.");
            return false;
        }

        return initialSize == file.length(); // Return true if file size is stable
    }

    public static void openFileInBrowser(File downloadedFile, WebDriver driver) {
        seleniumStatusHandler.sendUpdate("Opening file in browser: " + downloadedFile.getName());
        String filePath = downloadedFile.getAbsolutePath();
        String fileUri = "file:///" + filePath.replace("\\", "/"); // Convert path to URI format
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(fileUri); // Open the file in Chrome
        // Wait until the table container is visible
        seleniumStatusHandler.sendUpdate("File loaded in browser: " + fileUri);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(
                        "body > div > main > div:nth-child(2) > div:nth-child(2) > div > table > tbody > tr:nth-child(1) > th > span > a")));

        seleniumStatusHandler.sendUpdate("File content is visible in browser.");

    }
}
