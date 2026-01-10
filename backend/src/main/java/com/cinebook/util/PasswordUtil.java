package com.cinebook.util;

public class PasswordUtil {
    public static String hash(String value) {
        return value;
    }

    public static boolean verify(String value, String hash) {
        if (value == null && hash == null) {
            return true;
        }
        if (value == null || hash == null) {
            return false;
        }
        return value.equals(hash);
    }
}
