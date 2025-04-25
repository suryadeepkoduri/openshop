package com.suryadeep.openshop.util;

import org.slf4j.MDC;

/**
 * Utility class for working with MDC (Mapped Diagnostic Context)
 * Provides methods to add, get, and remove values from the MDC context
 */
public class MdcUtil {

    private MdcUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Add a value to the MDC context
     * @param key The key to use
     * @param value The value to add
     */
    public static void put(String key, String value) {
        if (key != null && value != null) {
            MDC.put(key, value);
        }
    }

    /**
     * Get a value from the MDC context
     * @param key The key to look up
     * @return The value associated with the key, or null if not found
     */
    public static String get(String key) {
        return MDC.get(key);
    }

    /**
     * Remove a value from the MDC context
     * @param key The key to remove
     */
    public static void remove(String key) {
        if (key != null) {
            MDC.remove(key);
        }
    }

    /**
     * Clear all values from the MDC context
     */
    public static void clear() {
        MDC.clear();
    }

    /**
     * Add a value to the MDC context for the duration of a runnable
     * The value will be removed after the runnable completes, even if an exception is thrown
     * @param key The key to use
     * @param value The value to add
     * @param runnable The code to execute with the MDC value set
     */
    public static void withMdc(String key, String value, Runnable runnable) {
        try {
            put(key, value);
            runnable.run();
        } finally {
            remove(key);
        }
    }
}