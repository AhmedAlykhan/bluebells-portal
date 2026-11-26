package com.bluebells.common;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Institutional constants and timezone grounding for Bluebells Schooling System (Hyderabad, Sindh, Pakistan).
 */
public final class SchoolConstants {

    public static final String TIMEZONE_ID = "Asia/Karachi";
    public static final ZoneId PK_ZONE = ZoneId.of(TIMEZONE_ID);
    public static final String CURRENCY_CODE = "PKR";

    private SchoolConstants() {
        // Prevent instantiation
    }

    /**
     * Obtains the current school calendar date in Asia/Karachi timezone.
     */
    public static LocalDate today() {
        return LocalDate.now(PK_ZONE);
    }
}
