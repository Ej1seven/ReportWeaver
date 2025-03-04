package com.reportweaver.reportweaver.service;

import com.reportweaver.reportweaver.model.Error;
import com.reportweaver.reportweaver.model.ErrorSummary;
import com.reportweaver.reportweaver.util.GoogleDocsFormatter;

import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.*;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;

import com.reportweaver.reportweaver.websocket.SeleniumStatusHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing Google Docs-related operations.
 * This service interacts with Google Docs and Drive APIs to create
 * accessibility reports,
 * format documents, and share them with specified users.
 */
@Lazy
@Service
public class GoogleDocsService {

        // Logger instance for logging Google Docs service activities.
        private static final Logger logger = LoggerFactory.getLogger(GoogleDocsService.class);

        // Google Docs API service instance for document creation and modification.
        private final Docs docsService;

        // Google Drive API service instance for file permissions and sharing.
        private final Drive driveService;

        // Utility class for formatting Google Docs content.
        private final GoogleDocsFormatter googleDocsFormatter;

        // WebSocket handler for sending real-time status updates to the frontend.
        private final SeleniumStatusHandler seleniumStatusHandler;

        /**
         * Constructs the GoogleDocsService with the necessary dependencies.
         *
         * @param googleAuthService     The authentication service that provides API
         *                              clients.
         * @param seleniumStatusHandler WebSocket handler for sending updates.
         */
        public GoogleDocsService(GoogleAuthService googleAuthService, SeleniumStatusHandler seleniumStatusHandler) {
                this.docsService = googleAuthService.getDocsService();
                this.driveService = googleAuthService.getDriveService();
                this.seleniumStatusHandler = seleniumStatusHandler;
                this.googleDocsFormatter = new GoogleDocsFormatter(this.docsService, this.seleniumStatusHandler);
        }

        /**
         * Asynchronously creates an accessibility report in Google Docs.
         * This method organizes extracted errors into a structured document and formats
         * the content.
         *
         * @param title                    The title of the report.
         * @param errors                   The list of extracted errors.
         * @param fileDataExtractorService Service instance for processing extracted
         *                                 errors.
         * @return A CompletableFuture containing the Google Doc ID upon successful
         *         creation.
         * @throws IOException          If an error occurs while interacting with the
         *                              Google Docs API.
         * @throws InterruptedException If the thread is interrupted while formatting
         *                              the document.
         */
        @Async
        public CompletableFuture<String> createAccessibilityReport(String title,
                        List<Error> errors,
                        FileDataExtractorService fileDataExtractorService)
                        throws IOException, InterruptedException {

                seleniumStatusHandler.sendUpdate("Starting Google Docs report creation...");

                // Generate a summary of extracted errors
                List<ErrorSummary> errorSummary = fileDataExtractorService.getErrorSummary(errors);
                List<Error> reversedErrors = new ArrayList<>(errors);
                Collections.reverse(reversedErrors);

                // Create a new Google Document
                seleniumStatusHandler.sendUpdate("Creating new Google Document...");
                Document doc = new Document().setTitle(title);
                doc = docsService.documents().create(doc).execute();
                String documentId = doc.getDocumentId();

                seleniumStatusHandler.sendUpdate("Google Document created with ID: " + documentId);
                logger.info("Created document with ID: {}", documentId);

                try {// Process and format each extracted error in the document
                        for (Error error : reversedErrors) {
                                seleniumStatusHandler.sendUpdate("Adding error details for: " + error.getErrorName());

                                // Add formatted error details to the document
                                googleDocsFormatter.addParagraph(documentId, "");
                                googleDocsFormatter.createErrorDetailsTable(documentId, error);
                                googleDocsFormatter.addParagraph(documentId, "");
                                googleDocsFormatter.addParagraph(documentId, error.getHowToFixIt());
                                googleDocsFormatter.createHeading(documentId, "How to fix it:", 5);
                                googleDocsFormatter.addParagraph(documentId, error.getWhyItMatters());
                                googleDocsFormatter.createHeading(documentId, "Why it matters:", 5);
                                googleDocsFormatter.createHeading(documentId, error.getErrorName(), 4);

                                // Delay between formatting operations to prevent API rate limits
                                Thread.sleep(1000);
                        }

                        // Add summary and error-by-page sections
                        seleniumStatusHandler.sendUpdate("Adding 'Errors by Page' section...");
                        googleDocsFormatter.createHeading(documentId, "Errors by Page", 3);
                        googleDocsFormatter.createErrorSummaryTable(documentId, errorSummary);

                        seleniumStatusHandler.sendUpdate("Adding 'Summary' section...");
                        googleDocsFormatter.addParagraph(documentId, "");
                        googleDocsFormatter.createHeading(documentId, "Summary", 3);

                        seleniumStatusHandler.sendUpdate("Google Docs report creation completed!");
                        return CompletableFuture.completedFuture(documentId);

                } catch (Exception e) {
                        seleniumStatusHandler.sendUpdate("Error creating Google Doc: " + e.getMessage());
                        logger.error("Error creating Google Doc", e);
                        return CompletableFuture.completedFuture("Processing");
                }
        }

        /**
         * Shares a Google Document with a specified email address by granting access
         * permissions.
         *
         * @param documentId   The ID of the Google Document to be shared.
         * @param emailAddress The email address of the recipient.
         * @throws IOException If an error occurs while setting permissions in Google
         *                     Drive.
         */
        public void shareDocument(String documentId, String emailAddress) throws IOException {
                seleniumStatusHandler.sendUpdate("Sharing Google Document with: " + emailAddress);

                // Define the permission settings (granting write access)
                Permission permission = new Permission()
                                .setType("user")
                                .setRole("writer") // Available roles: writer, reader, owner
                                .setEmailAddress(emailAddress);

                // Apply the permission to the document
                driveService.permissions().create(documentId, permission)
                                .setSendNotificationEmail(true) // Sends an email notification to the recipient
                                .execute();

                seleniumStatusHandler.sendUpdate("Google Document shared with " + emailAddress);
                logger.info("Document shared with {}", emailAddress);
        }
}
