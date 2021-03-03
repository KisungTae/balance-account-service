package com.beeswork.balanceaccountservice.util;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static int getYearFrom(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static String toISOString(Date date) {
        return DateTimeFormatter.ISO_INSTANT.format(date.toInstant());
    }
}
