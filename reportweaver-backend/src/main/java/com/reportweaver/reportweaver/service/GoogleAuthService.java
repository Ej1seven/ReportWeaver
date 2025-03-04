package com.reportweaver.reportweaver.service;

import com.google.api.services.docs.v1.Docs;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.client.json.gson.GsonFactory;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Service responsible for authenticating and managing Google API credentials.
 * This service initializes authentication for Google Docs and Google Drive
 * APIs.
 */
@Service
public class GoogleAuthService {

    // Logger instance for logging authentication-related information and errors.
    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthService.class);

    // Application name used for API requests.
    private static final String APPLICATION_NAME = "Report Weaver";

    // JSON factory instance used for API communication.
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // Loads environment variables from the .env file.
    private static final Dotenv dotenv = Dotenv.load();

    // Google Docs API service instance.
    private final Docs docsService;

    // Google Drive API service instance.
    private final Drive driveService;

    /**
     * Constructs the GoogleAuthService and initializes API clients for Google Docs
     * and Drive.
     *
     * @throws IOException If authentication fails due to incorrect credentials or
     *                     file issues.
     */
    public GoogleAuthService() throws IOException {
        GoogleCredentials credentials = loadGoogleCredentials();
        HttpCredentialsAdapter httpCredentialsAdapter = new HttpCredentialsAdapter(credentials);

        // Initialize the Google Docs API client
        this.docsService = new Docs.Builder(
                new com.google.api.client.http.javanet.NetHttpTransport(),
                JSON_FACTORY,
                httpCredentialsAdapter)
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Initialize the Google Drive API client
        this.driveService = new Drive.Builder(
                new com.google.api.client.http.javanet.NetHttpTransport(),
                JSON_FACTORY,
                httpCredentialsAdapter)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Loads Google API credentials from an external file.
     *
     * @return A GoogleCredentials instance for API authentication.
     * @throws IOException If the credentials file is missing, malformed, or cannot
     *                     be accessed.
     */
    private GoogleCredentials loadGoogleCredentials() throws IOException {
        // Retrieve the credentials file path from environment variables
        String credentialsPath = dotenv.get("GOOGLE_CREDENTIALS_JSON");

        if (credentialsPath == null || credentialsPath.isEmpty()) {
            throw new IllegalStateException("GOOGLE_CREDENTIALS_JSON environment variable is missing.");
        }

        // Load credentials from the specified file
        try (FileInputStream credentialsStream = new FileInputStream(credentialsPath)) {
            return GoogleCredentials.fromStream(credentialsStream)
                    .createScoped(List.of(
                            "https://www.googleapis.com/auth/documents",
                            "https://www.googleapis.com/auth/drive"));
        } catch (Exception e) {
            logger.error("Failed to load Google credentials from file: {}", e.getMessage(), e);
            throw new IOException("Error loading credentials from file.", e);
        }
    }

    /**
     * Retrieves the initialized Google Docs service instance.
     *
     * @return The Google Docs API client.
     */
    public Docs getDocsService() {
        return docsService;
    }

    /**
     * Retrieves the initialized Google Drive service instance.
     *
     * @return The Google Drive API client.
     */
    public Drive getDriveService() {
        return driveService;
    }
}
