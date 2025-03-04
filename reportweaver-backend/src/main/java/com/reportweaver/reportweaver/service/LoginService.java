package com.reportweaver.reportweaver.service;

import com.reportweaver.reportweaver.websocket.SeleniumStatusHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import org.testng.Assert;

import java.time.Duration;

/**
 * Service responsible for handling the login process using Selenium WebDriver.
 * This service automates authentication by interacting with login fields,
 * handling Single Sign-On (SSO), and managing multi-factor authentication
 * (MFA).
 */
@Lazy
@Service
public class LoginService {

    // Maximum wait time for login-related elements to be located.
    private final Duration extendedWaitTimeout;

    // WebSocket handler for sending real-time status updates to the frontend.
    private final SeleniumStatusHandler seleniumStatusHandler;

    /**
     * Constructs the LoginService with the required dependencies.
     *
     * @param extendedWaitTimeout   Timeout duration for waiting on login elements.
     * @param seleniumStatusHandler WebSocket handler for sending status updates.
     */
    public LoginService(Duration extendedWaitTimeout, SeleniumStatusHandler seleniumStatusHandler) {
        this.extendedWaitTimeout = extendedWaitTimeout;
        this.seleniumStatusHandler = seleniumStatusHandler;
    }

    /**
     * Automates the login process by navigating to the login page,
     * filling in credentials, handling SSO login, and completing authentication.
     *
     * @param driver   Selenium WebDriver instance used for browser automation.
     * @param url      URL of the login page. If null, assumes the page is already
     *                 loaded.
     * @param username The username for authentication.
     * @param password The password for authentication.
     */
    public void performLogin(WebDriver driver, String url, String username, String password) {
        WebDriverWait wait = new WebDriverWait(driver, extendedWaitTimeout);
        try {
            seleniumStatusHandler.sendUpdate("Starting login process...");

            // Navigate to the login page if a URL is provided
            if (url != null && !url.trim().isEmpty()) {
                seleniumStatusHandler.sendUpdate("Navigating to login page: " + url);
                driver.get(url);
            }

            seleniumStatusHandler.sendUpdate("Waiting for username field...");
            WebElement usernameInputField = wait
                    .until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));

            seleniumStatusHandler.sendUpdate("Entering username...");
            usernameInputField.sendKeys(username);

            seleniumStatusHandler.sendUpdate("Checking SSO login mode...");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#login-mode-sso")));

            // Verify if SSO is enabled
            Assert.assertTrue("true".equals(driver.findElement(By.id("sso-enabled")).getAttribute("value")),
                    "Hidden input field value is not 'true'. Test failed.");

            seleniumStatusHandler.sendUpdate("Clicking login button...");
            WebElement loginButton = driver.findElement(By.cssSelector(
                    "#login-form > div.form-group.form-submission > div:nth-child(1) > div:nth-child(1) > button"));
            loginButton.click();

            seleniumStatusHandler.sendUpdate("Waiting for credential fields...");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));

            seleniumStatusHandler.sendUpdate("Entering credentials...");
            driver.findElement(By.id("username")).sendKeys(username);
            driver.findElement(By.id("password")).sendKeys(password);

            seleniumStatusHandler.sendUpdate("Submitting login form...");
            driver.findElement(By.cssSelector("#main-content > div.idp3_form-submit-container > button")).click();

            seleniumStatusHandler.sendUpdate("Checking for Duo authentication...");
            WebElement trustedLoginButton = wait
                    .until(ExpectedConditions.visibilityOfElementLocated(By.id("trust-browser-button")));
            trustedLoginButton.click();

            seleniumStatusHandler.sendUpdate("Login successful!");
        } catch (Exception e) {
            seleniumStatusHandler.sendUpdate("Login failed: " + e.getMessage());

            // Log the exception and print error details
            e.printStackTrace();
            System.err.println("An error occurred during login: " + e.getMessage());
        }
    }
}
