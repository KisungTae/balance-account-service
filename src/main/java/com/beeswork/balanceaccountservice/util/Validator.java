package com.beeswork.balanceaccountservice.util;

import com.beeswork.balanceaccountservice.constant.RegexExpression;
import com.beeswork.balanceaccountservice.exception.BadRequestException;

import java.util.regex.Pattern;

public class Validator {

    private static final Pattern VALID_UUID_PATTERN = Pattern.compile(RegexExpression.VALID_UUID);

    public static boolean validateUUID(String uuid) {
        return (uuid != null && VALID_UUID_PATTERN.matcher(uuid).find());
    }

    public static boolean validateNumber(String number) {
        if (number == null) return false;
        int length = number.length();
        if (length == 0) return false;
        int i = 0;
        if (number.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = number.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}

