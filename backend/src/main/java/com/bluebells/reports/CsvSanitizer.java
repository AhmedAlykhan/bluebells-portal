package com.bluebells.reports;

public final class CsvSanitizer {

    private CsvSanitizer() {
    }

    /**
     * Sanitizes a cell value to prevent CSV / Formula Injection (CWE-1236).
     * If value starts with '=', '+', '-', '@', '\t', '\r', it prepends a single quote.
     * Also wraps in quotes if it contains commas or quotes.
     */
    public static String sanitize(Object raw) {
        if (raw == null) {
            return "";
        }
        String value = raw.toString();
        if (value.isEmpty()) {
            return "";
        }

        char firstChar = value.charAt(0);
        if (firstChar == '=' || firstChar == '+' || firstChar == '-' || firstChar == '@'
                || firstChar == '\t' || firstChar == '\r') {
            value = "'" + value;
        }

        if (value.contains("\"")) {
            value = value.replace("\"", "\"\"");
        }

        if (value.contains(",") || value.contains("\n") || value.contains("\"") || value.contains("'")) {
            value = "\"" + value + "\"";
        }

        return value;
    }
}
