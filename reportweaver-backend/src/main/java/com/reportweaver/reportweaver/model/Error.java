package com.reportweaver.reportweaver.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an error entry with details about its occurrences, category,
 * documentation,
 * and remediation steps.
 * This class also keeps track of associated data entries and calculates the
 * total number of errors.
 */
public class Error {

    // ✅ The number of instances where this error occurs.
    private final int instanceCount;

    // ✅ The name or identifier of the error.
    private final String errorName;

    // ✅ The category or classification of the error.
    private final String errorCategory;

    // ✅ Documentation URL or information related to the error.
    private final String errorDocumentation;

    // ✅ Explanation of why this error is significant.
    private final String whyItMatters;

    // ✅ Steps to fix the error.
    private final String howToFixIt;

    // ✅ List of associated data entries where this error occurs.
    private final List<DataEntry> dataEntries;

    // ✅ The total number of occurrences of this error.
    private int totalErrors;

    /**
     * Constructs an Error object with the provided details.
     *
     * @param instanceCount      Number of times this error occurs.
     * @param errorName          The name or identifier of the error.
     * @param errorCategory      The category or type of the error.
     * @param errorDocumentation Link or reference to documentation explaining the
     *                           error.
     * @param whyItMatters       Explanation of the impact of this error.
     * @param howToFixIt         Recommended steps to resolve the error.
     */
    public Error(int instanceCount, String errorName, String errorCategory, String errorDocumentation,
            String whyItMatters, String howToFixIt) {
        this.instanceCount = instanceCount;
        this.errorName = errorName;
        this.errorCategory = errorCategory;
        this.errorDocumentation = errorDocumentation;
        this.whyItMatters = whyItMatters;
        this.howToFixIt = howToFixIt;
        this.dataEntries = new ArrayList<>();
        this.totalErrors = 0;
    }

    /**
     * Retrieves the number of instances where this error occurs.
     *
     * @return The instance count of the error.
     */
    public int getInstanceCount() {
        return instanceCount;
    }

    /**
     * Retrieves the name of the error.
     *
     * @return The error name as a string.
     */
    public String getErrorName() {
        return errorName;
    }

    /**
     * Retrieves the category of the error.
     *
     * @return The error category as a string.
     */
    public String getErrorCategory() {
        return errorCategory;
    }

    /**
     * Retrieves the documentation link or reference for this error.
     *
     * @return The error documentation as a string.
     */
    public String getErrorDocumentation() {
        return errorDocumentation;
    }

    /**
     * Retrieves the explanation of why this error is significant.
     *
     * @return A string describing why the error matters.
     */
    public String getWhyItMatters() {
        return whyItMatters;
    }

    /**
     * Retrieves the recommended steps to fix this error.
     *
     * @return A string describing how to fix the error.
     */
    public String getHowToFixIt() {
        return howToFixIt;
    }

    /**
     * Retrieves the list of data entries associated with this error.
     *
     * @return A list of {@link DataEntry} objects.
     */
    public List<DataEntry> getDataEntries() {
        return dataEntries;
    }

    /**
     * Retrieves the total number of occurrences of this error.
     *
     * @return The total count of the error.
     */
    public int getTotalErrors() {
        return totalErrors;
    }

    /**
     * Adds a data entry to the list and updates the total error count.
     *
     * @param url   The URL where the error occurs.
     * @param count The number of times the error occurs at the given URL.
     */
    public void addDataEntry(String url, int count) {
        this.dataEntries.add(new DataEntry(url, count));
        updateTotalErrors();
    }

    /**
     * Updates the total error count by summing up occurrences from all data
     * entries.
     */
    private void updateTotalErrors() {
        this.totalErrors = dataEntries.stream().mapToInt(DataEntry::getCount).sum();
    }
}

// package com.reportweaver.reportweaver.model;

// import java.util.ArrayList;
// import java.util.List;

// public class Error {
// private final int instanceCount;
// private final String errorName;
// private final String errorCategory;
// private final String errorDocumentation;
// private final String whyItMatters;
// private final String howToFixIt;
// private final List<DataEntry> dataEntries;
// private int totalErrors;

// public Error(int instanceCount, String errorName, String errorCategory,
// String errorDocumentation,
// String whyItMatters, String howToFixIt) {
// this.instanceCount = instanceCount;
// this.errorName = errorName;
// this.errorCategory = errorCategory;
// this.errorDocumentation = errorDocumentation;
// this.whyItMatters = whyItMatters;
// this.howToFixIt = howToFixIt;
// this.dataEntries = new ArrayList<>();
// this.totalErrors = 0;
// }

// public int getInstanceCount() {
// return instanceCount;
// }

// public String getErrorName() {
// return errorName;
// }

// public String getErrorCategory() {
// return errorCategory;
// }

// public String getErrorDocumentation() {
// return errorDocumentation;
// }

// public String getWhyItMatters() {
// return whyItMatters;
// }

// public String getHowToFixIt() {
// return howToFixIt;
// }

// public List<DataEntry> getDataEntries() {
// return dataEntries;
// }

// public int getTotalErrors() {
// return totalErrors;
// }

// public void addDataEntry(String url, int count) {
// this.dataEntries.add(new DataEntry(url, count));
// updateTotalErrors();
// }

// private void updateTotalErrors() {
// this.totalErrors = dataEntries.stream().mapToInt(DataEntry::getCount).sum();
// }
// }
