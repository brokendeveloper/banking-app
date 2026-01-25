package com.brokendev.backend.account.utils;

public class AccountUtils {
    public static String generateAccountNumber() {
        return String.valueOf(System.currentTimeMillis());
    }
}
