package com.reportweaver.reportweaver.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Utility class for common web scraping operations using Selenium.
 * Provides helper methods to wait for elements, extract text, attributes,
 * and perform navigations within a web page.
 */
public class WebScraperUtils {

    // CSS selector for locating table rows in the section-body-table
    public static final String ROW_SELECTOR = ".section-body-table > table > tbody > tr";

    // CSS selector for locating error link elements
    public static final String ERROR_LINK_SELECTOR = "body > div > main > div:nth-child(2) > div:nth-child(4) > div:nth-child(3) > div > div > table > tbody > tr:nth-child(1) > th > a";

    // CSS selector for locating detailed error links inside the table
    public static final String ERROR_DETAIL_SELECTOR = ".table > tbody > tr > td:nth-child(7) > a";

    // CSS selector for locating rows that contain error counts
    public static final String ERROR_COUNT_ROW_SELECTOR = ".data-table .data-table-row-wrapper tr";

    /**
     * Waits for multiple elements to be present on the page.
     *
     * @param wait     WebDriverWait instance for waiting
     * @param selector The By locator to find elements
     * @return List of WebElements once they are present
     */
    public static List<WebElement> waitForElements(WebDriverWait wait, By selector) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(selector));
    }

    /**
     * Waits for a single element to become visible.
     *
     * @param wait     WebDriverWait instance for waiting
     * @param selector The By locator to find the element
     * @return The visible WebElement
     */
    public static WebElement waitForElement(WebDriverWait wait, By selector) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
    }

    /**
     * Retrieves the text content of an element found using a CSS selector.
     *
     * @param wait        WebDriverWait instance for waiting
     * @param cssSelector The CSS selector of the element
     * @return The text content of the element
     */
    public static String getElementText(WebDriverWait wait, String cssSelector) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssSelector))).getText();
    }

    /**
     * Retrieves the text content of a child element inside a given parent element.
     *
     * @param wait        WebDriverWait instance for waiting
     * @param parent      The parent WebElement
     * @param cssSelector The CSS selector of the child element
     * @return The text content of the child element
     */
    public static String getElementText(WebDriverWait wait, WebElement parent, String cssSelector) {
        return wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(parent, By.cssSelector(cssSelector)))
                .getText();
    }

    /**
     * Retrieves a specific attribute (e.g., href, src) from a child element inside
     * a given parent element.
     *
     * @param wait        WebDriverWait instance for waiting
     * @param parent      The parent WebElement
     * @param cssSelector The CSS selector of the child element
     * @param attribute   The attribute to retrieve (e.g., "href", "src")
     * @return The value of the specified attribute
     */
    public static String getAttribute(WebDriverWait wait, WebElement parent, String cssSelector, String attribute) {
        return wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(parent, By.cssSelector(cssSelector)))
                .getAttribute(attribute);
    }

    /**
     * Navigates to the "Reports" page by interacting with the navigation menu.
     *
     * @param wait   WebDriverWait instance for waiting
     * @param driver WebDriver instance used for interacting with the page
     */
    public static void navigateToReports(WebDriverWait wait, WebDriver driver) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"left-sidebar\"]/div/app-navigation/div/ul/li[2]/button"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Reports"))).click();
    }

    /**
     * Retrieves all report rows from the reports table.
     *
     * @param wait   WebDriverWait instance for waiting
     * @param driver WebDriver instance used for interacting with the page
     * @return List of WebElements representing the rows in the reports table
     */
    public static List<WebElement> getReportRows(WebDriverWait wait, WebDriver driver) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("#reports-table > data-table > div > div tbody tr")));
    }
}
