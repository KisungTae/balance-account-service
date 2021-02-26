package com.beeswork.balanceaccountservice.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static int getYearFrom(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }
}
