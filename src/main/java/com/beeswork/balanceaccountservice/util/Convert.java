package com.beeswork.balanceaccountservice.util;

import com.google.common.base.Strings;

import java.util.UUID;
import java.util.regex.Pattern;

public class Convert {

    private static final Pattern UUID_REGEX_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    public static UUID toUUIDOrThrow(String uuid, RuntimeException runtimeException) {
        if (Strings.isNullOrEmpty(uuid)) throw runtimeException;
        if (!UUID_REGEX_PATTERN.matcher(uuid).matches()) throw runtimeException;
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException exception) {
            throw runtimeException;
        }
    }

    public static UUID toUUID(String uuid) {
        if (Strings.isNullOrEmpty(uuid)) return null;
        if (!UUID_REGEX_PATTERN.matcher(uuid).matches()) return null;
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    public static long toLongOrThrow(String number, RuntimeException runtimeException) {
        try {
            if (number == null) throw runtimeException;
            return Long.parseLong(number);
        } catch (NumberFormatException numberFormatException) {
            throw runtimeException;
        }
    }

    public static boolean toBooleanOrThrow(String bool, RuntimeException runtimeException) {
        if (bool == null) throw runtimeException;
        return Boolean.parseBoolean(bool);
    }
}
