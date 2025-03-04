package com.reportweaver.reportweaver.util;

import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.*;
import com.reportweaver.reportweaver.model.DataEntry;
import com.reportweaver.reportweaver.model.Error;
import com.reportweaver.reportweaver.model.ErrorSummary;
import com.reportweaver.reportweaver.websocket.SeleniumStatusHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleDocsFormatter {

        private final Docs docsService;
        private final SeleniumStatusHandler seleniumStatusHandler;

        public GoogleDocsFormatter(Docs docsService, SeleniumStatusHandler seleniumStatusHandler) {
                this.docsService = docsService;
                this.seleniumStatusHandler = seleniumStatusHandler;
        }

        public void createHeading(String documentId, String text, int headingLevel)
                        throws IOException {
                seleniumStatusHandler.sendUpdate("Adding heading: " + text);
                // Create a request to insert the heading text
                Request insertTextRequest = new Request().setInsertText(new InsertTextRequest()
                                .setText(text + "\n")
                                .setLocation(new Location().setIndex(1)));

                // Create a request to format the inserted text as a heading
                Request headingStyleRequest = new Request().setUpdateParagraphStyle(new UpdateParagraphStyleRequest()
                                .setFields("namedStyleType")
                                .setParagraphStyle(new ParagraphStyle().setNamedStyleType("HEADING_" + headingLevel))
                                .setRange(new Range().setStartIndex(1).setEndIndex(1 + text.length())));

                // Combine both requests into a batch
                BatchUpdateDocumentRequest body = new BatchUpdateDocumentRequest()
                                .setRequests(List.of(insertTextRequest, headingStyleRequest));
                // Execute the batch request
                docsService.documents().batchUpdate(documentId, body).execute();
        }

        public void addParagraph(String documentId, String text) throws IOException {
                seleniumStatusHandler.sendUpdate("Adding paragraph: " + (text.isEmpty() ? "[Empty Line]" : text));
                List<Request> requests = new ArrayList<>();

                // ✅ Request to insert the text
                requests.add(new Request().setInsertText(new InsertTextRequest()
                                .setText(text + "\n") // Insert the text with a newline
                                .setLocation(new Location().setIndex(1)))); // Insert at the beginning of the document

                // ✅ Request to set the paragraph style to "NORMAL_TEXT"
                requests.add(new Request().setUpdateParagraphStyle(new UpdateParagraphStyleRequest()
                                .setFields("namedStyleType") // Specify that only the namedStyleType is being updated
                                .setParagraphStyle(new ParagraphStyle().setNamedStyleType("NORMAL_TEXT"))
                                .setRange(new Range().setStartIndex(1).setEndIndex(1 + text.length())))); // Apply style
                                                                                                          // to
                                                                                                          // inserted
                                                                                                          // text

                // ✅ Execute the batch update
                BatchUpdateDocumentRequest body = new BatchUpdateDocumentRequest().setRequests(requests);
                docsService.documents().batchUpdate(documentId, body).execute();
        }

        public void createErrorSummaryTable(String documentId, List<ErrorSummary> summaryList)
                        throws IOException {
                seleniumStatusHandler.sendUpdate("Creating error summary table...");
                List<Request> requests = new ArrayList<>();
                int insertionIndex = 1; // Start index for inserting text in the document

                // ✅ Insert Table Header (Quantity | Errors)
                String tableHeader = "Quantity\tErrors\n"; // Double tab for better alignment
                requests.add(new Request().setInsertText(new InsertTextRequest()
                                .setText(tableHeader)
                                .setLocation(new Location().setIndex(insertionIndex))));

                // ✅ Apply NORMAL_TEXT style to header
                requests.add(new Request().setUpdateParagraphStyle(new UpdateParagraphStyleRequest()
                                .setFields("namedStyleType")
                                .setParagraphStyle(new ParagraphStyle().setNamedStyleType("NORMAL_TEXT"))
                                .setRange(new Range().setStartIndex(insertionIndex)
                                                .setEndIndex(insertionIndex + tableHeader.length()))));

                // ✅ Apply BOLD style to header
                requests.add(new Request().setUpdateTextStyle(new UpdateTextStyleRequest()
                                .setFields("bold")
                                .setTextStyle(new TextStyle().setBold(true))
                                .setRange(new Range().setStartIndex(insertionIndex)
                                                .setEndIndex(insertionIndex + tableHeader.length()))));

                insertionIndex += tableHeader.length(); // Move to next line

                // ✅ Loop through summary data to ensure proper alignment
                for (ErrorSummary summary : summaryList) {
                        // Ensure proper tab spacing for alignment
                        String rowText = summary.getTotalErrors() + "\t\t" + summary.getErrorName() + "\n"; // Double
                                                                                                            // tab space

                        // ✅ Insert the row into the document
                        requests.add(new Request().setInsertText(new InsertTextRequest()
                                        .setText(rowText)
                                        .setLocation(new Location().setIndex(insertionIndex))));

                        // ✅ Apply NORMAL_TEXT style to row
                        requests.add(new Request().setUpdateParagraphStyle(new UpdateParagraphStyleRequest()
                                        .setFields("namedStyleType")
                                        .setParagraphStyle(new ParagraphStyle().setNamedStyleType("NORMAL_TEXT"))
                                        .setRange(new Range().setStartIndex(insertionIndex)
                                                        .setEndIndex(insertionIndex + rowText.length()))));

                        insertionIndex += rowText.length(); // Move to next line
                }

                // ✅ Execute batch update request if there are changes
                if (!requests.isEmpty()) {
                        BatchUpdateDocumentRequest body = new BatchUpdateDocumentRequest().setRequests(requests);
                        docsService.documents().batchUpdate(documentId, body).execute();
                }
        }

        public void createErrorDetailsTable(String documentId, Error error) throws IOException {
                seleniumStatusHandler.sendUpdate("Creating error details table for: " + error.getErrorName());

                List<Request> requests = new ArrayList<>();
                int insertionIndex = 1; // Start at the beginning of the document

                // ✅ Insert Table Header ("Errors | Title")
                String tableHeader = "Errors\t\tTitle\n"; // One tab for spacing
                requests.add(new Request().setInsertText(new InsertTextRequest()
                                .setText(tableHeader)
                                .setLocation(new Location().setIndex(insertionIndex))));

                // ✅ Apply NORMAL_TEXT style to header
                requests.add(new Request().setUpdateParagraphStyle(new UpdateParagraphStyleRequest()
                                .setFields("namedStyleType")
                                .setParagraphStyle(new ParagraphStyle().setNamedStyleType("NORMAL_TEXT"))
                                .setRange(new Range()
                                                .setStartIndex(insertionIndex)
                                                .setEndIndex(insertionIndex + tableHeader.length()))));

                // ✅ Apply BOLD style to header
                requests.add(new Request().setUpdateTextStyle(new UpdateTextStyleRequest()
                                .setFields("bold")
                                .setTextStyle(new TextStyle().setBold(true))
                                .setRange(new Range().setStartIndex(insertionIndex)
                                                .setEndIndex(insertionIndex + tableHeader.length()))));

                // ✅ Move insertion index after the header
                insertionIndex += tableHeader.length();

                // ✅ Loop through error data entries and format rows
                for (DataEntry entry : error.getDataEntries()) {
                        String countText = entry.getCount() + "\t\t"; // Tab after the count
                        String url = entry.getUrl(); // Full URL
                        String linkText = extractTitleFromUrl(url) + "\n"; // Readable title from the URL

                        // ✅ Insert count text
                        requests.add(new Request().setInsertText(new InsertTextRequest()
                                        .setText(countText)
                                        .setLocation(new Location().setIndex(insertionIndex))));
                        int countEndIndex = insertionIndex + countText.length();

                        // ✅ Apply NORMAL_TEXT style to count text
                        requests.add(new Request().setUpdateParagraphStyle(new UpdateParagraphStyleRequest()
                                        .setFields("namedStyleType")
                                        .setParagraphStyle(new ParagraphStyle().setNamedStyleType("NORMAL_TEXT"))
                                        .setRange(new Range()
                                                        .setStartIndex(insertionIndex)
                                                        .setEndIndex(countEndIndex))));

                        // ✅ Insert link text (readable title)
                        requests.add(new Request().setInsertText(new InsertTextRequest()
                                        .setText(linkText)
                                        .setLocation(new Location().setIndex(countEndIndex))));
                        int linkEndIndex = countEndIndex + linkText.length();

                        // ✅ Apply NORMAL_TEXT style to link text
                        requests.add(new Request().setUpdateParagraphStyle(new UpdateParagraphStyleRequest()
                                        .setFields("namedStyleType")
                                        .setParagraphStyle(new ParagraphStyle().setNamedStyleType("NORMAL_TEXT"))
                                        .setRange(new Range()
                                                        .setStartIndex(countEndIndex)
                                                        .setEndIndex(linkEndIndex))));

                        // ✅ Add hyperlink to the link text
                        requests.add(new Request().setUpdateTextStyle(new UpdateTextStyleRequest()
                                        .setFields("link")
                                        .setTextStyle(new TextStyle().setLink(new Link().setUrl(url))) // Link points to
                                                                                                       // full URL
                                        .setRange(new Range()
                                                        .setStartIndex(countEndIndex)
                                                        .setEndIndex(linkEndIndex))));

                        // ✅ Update insertion index for the next row
                        insertionIndex = linkEndIndex;
                }

                // ✅ Execute batch update if there are changes
                if (!requests.isEmpty()) {
                        BatchUpdateDocumentRequest body = new BatchUpdateDocumentRequest().setRequests(requests);
                        docsService.documents().batchUpdate(documentId, body).execute();
                }
        }

        /**
         * Extracts a readable title from the last section of a URL by:
         * - Splitting the URL by "/" and taking the last segment
         * - Replacing hyphens with spaces
         * - Capitalizing each word
         */
        private static String extractTitleFromUrl(String url) {
                if (url == null || url.isEmpty()) {
                        return "Untitled"; // Provide a default title for empty or null URLs
                }

                // Split the URL into segments by "/"
                String[] parts = url.split("/");
                if (parts.length == 0) {
                        return "Untitled"; // Handle edge case where split results in an empty array
                }

                // Get the last segment of the URL
                String lastSegment = parts[parts.length - 1];
                if (lastSegment.isEmpty()) {
                        return "Untitled"; // Handle edge case where the last segment is empty
                }

                // Replace hyphens with spaces and capitalize each word
                String[] words = lastSegment.split("-");
                StringBuilder title = new StringBuilder();
                for (String word : words) {
                        if (!word.isEmpty()) {
                                title.append(Character.toUpperCase(word.charAt(0))) // Capitalize first letter
                                                .append(word.substring(1).toLowerCase()) // Lowercase the rest
                                                .append(" ");
                        }
                }
                return title.toString().trim(); // Return the formatted title
        }
}
