package com.beeswork.balanceaccountservice.util;

import java.util.UUID;

public class Convert {

    public static UUID toUUIDOrThrow(String uuid, RuntimeException runtimeException) {
        try {
            if (uuid == null) throw runtimeException;
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException exception) {
            throw runtimeException;
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
