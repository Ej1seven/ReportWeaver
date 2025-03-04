package com.reportweaver.reportweaver.model;

/**
 * Represents extracted data from a report or web scraping process.
 * This model is used to store key details such as the report title, the first
 * table cell,
 * and a brief description.
 */
public class ExtractedData {

    // ✅ The title of the extracted data entry.
    private final String title;

    // ✅ The first cell of the relevant data table.
    private final String firstTableCell;

    // ✅ A brief description or summary of the extracted data.
    private final String description;

    /**
     * Constructs an ExtractedData object with the provided details.
     *
     * @param title          The title of the extracted data.
     * @param firstTableCell The first table cell value related to the extracted
     *                       data.
     * @param description    A brief description of the extracted data.
     */
    public ExtractedData(String title, String firstTableCell, String description) {
        this.title = title;
        this.firstTableCell = firstTableCell;
        this.description = description;
    }

    /**
     * Retrieves the title of the extracted data.
     *
     * @return The extracted data title as a string.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retrieves the first table cell value.
     *
     * @return The first cell value as a string.
     */
    public String getFirstTableCell() {
        return firstTableCell;
    }

    /**
     * Retrieves the description of the extracted data.
     *
     * @return A brief description as a string.
     */
    public String getDescription() {
        return description;
    }
}

// package com.reportweaver.reportweaver.model;

// public class ExtractedData {
// private final String title;
// private final String firstTableCell;
// private final String description;

// public ExtractedData(String title, String firstTableCell, String description)
// {
// this.title = title;
// this.firstTableCell = firstTableCell;
// this.description = description;
// }

// public String getTitle() {
// return title;
// }

// public String getFirstTableCell() {
// return firstTableCell;
// }

// public String getDescription() {
// return description;
// }
// }
