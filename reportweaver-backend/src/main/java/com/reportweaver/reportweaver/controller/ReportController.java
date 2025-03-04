package com.reportweaver.reportweaver.controller;

import com.reportweaver.reportweaver.service.ReportService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for handling report generation requests.
 * This controller exposes an endpoint for processing reports asynchronously.
 */
@RestController
@RequestMapping("")
@CrossOrigin(origins = "http://localhost:5173") // Allow frontend requests
public class ReportController {

    private final ReportService reportService;

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    // Use a dedicated thread pool to avoid ForkJoinPool exhaustion
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * Constructor-based dependency injection for ReportService.
     *
     * @param reportService Service responsible for handling report generation
     *                      logic.
     */
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * DTO (Data Transfer Object) class to represent the incoming JSON request
     * payload.
     */
    public static class ReportRequest {
        public String website;
        public String username;
        public String password;
        public String email;
    }

    /**
     * Handles POST requests for generating a report.
     * The processing is performed asynchronously using {@link DeferredResult}.
     *
     * @param request The request payload containing report parameters.
     * @return A {@link DeferredResult} containing the generated document ID or an
     *         error response.
     */
    @PostMapping("/")
    public DeferredResult<ResponseEntity<String>> generateReport(@RequestBody ReportRequest request) {
        DeferredResult<ResponseEntity<String>> deferredResult = new DeferredResult<>(600000L); // 10-minute timeout

        // Handle timeout to prevent 503 errors
        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Request timed out. Please try again later."));
            logger.error("Report generation request timed out.");
        });

        // Execute asynchronously with a dedicated thread pool
        executorService.submit(() -> {
            try {
                String documentId = reportService.runReportProcess(
                        request.website, request.username, request.password, request.email).get();
                deferredResult.setResult(ResponseEntity.ok(documentId));
                logger.info("Document ID: " + documentId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
                deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Request was interrupted. Try again later."));
                logger.error("Report generation was interrupted.");
            } catch (Exception e) {
                deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error generating report: " + e.getMessage()));
                logger.error("Error generating report: " + e.getMessage());
            }
        });
        return deferredResult;
    }

}
