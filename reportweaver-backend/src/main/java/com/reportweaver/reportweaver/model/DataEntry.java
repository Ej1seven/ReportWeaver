package com.reportweaver.reportweaver.model;

/**
 * Represents a data entry containing a URL and an associated count.
 * This model is used to store and process extracted data, particularly in web
 * scraping
 * and data aggregation processes.
 */
public class DataEntry {

    // ✅ The URL associated with the data entry.
    private final String url;

    // ✅ The count of occurrences for the given URL.
    private final int count;

    /**
     * Constructs a new DataEntry object.
     *
     * @param url   The URL related to the entry.
     * @param count The number of occurrences associated with the URL.
     */
    public DataEntry(String url, int count) {
        this.url = url;
        this.count = count;
    }

    /**
     * Retrieves the URL of the data entry.
     *
     * @return The associated URL as a string.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Retrieves the count of occurrences for the given URL.
     *
     * @return The count as an integer.
     */
    public int getCount() {
        return count;
    }
}

// package com.reportweaver.reportweaver.model;

// public class DataEntry {
// private final String url;
// private final int count;

// public DataEntry(String url, int count) {
// this.url = url;
// this.count = count;
// }

// public String getUrl() {
// return url;
// }

// public int getCount() {
// return count;
// }
// }
