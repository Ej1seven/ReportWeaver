package com.reportweaver.reportweaver.model;

/**
 * Represents a summary of errors, including the error name and the total number
 * of occurrences.
 * This class is typically used for reporting and analysis purposes.
 */
public class ErrorSummary {

    // ✅ The name or type of the error.
    private final String errorName;

    // ✅ The total number of times this error has occurred.
    private final int totalErrors;

    /**
     * Constructs an ErrorSummary object with the provided error name and total
     * occurrences.
     *
     * @param errorName   The name or type of the error.
     * @param totalErrors The total number of times the error has occurred.
     */
    public ErrorSummary(String errorName, int totalErrors) {
        this.errorName = errorName;
        this.totalErrors = totalErrors;
    }

    /**
     * Retrieves the name of the error.
     *
     * @return The name of the error as a string.
     */
    public String getErrorName() {
        return errorName;
    }

    /**
     * Retrieves the total number of occurrences of the error.
     *
     * @return The total count of the error.
     */
    public int getTotalErrors() {
        return totalErrors;
    }
}

// package com.reportweaver.reportweaver.model;

// public class ErrorSummary {
// private final String errorName;
// private final int totalErrors;

// public ErrorSummary(String errorName, int totalErrors) {
// this.errorName = errorName;
// this.totalErrors = totalErrors;
// }

// public String getErrorName() {
// return errorName;
// }

// public int getTotalErrors() {
// return totalErrors;
// }
// }
