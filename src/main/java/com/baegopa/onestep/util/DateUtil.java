package com.baegopa.onestep.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
    public static String getDateTime(String format) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(now);
    }


    public static String getDateTime() {
        return getDateTime("yyyy.MM.dd");
    }

    public static String getLongDateTime(Integer time) {
        return getLongDateTime(time, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getLongDateTime(Object time) {
        return getLongDateTime((Integer) time, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getLongDateTime(Object time, String format) {
        return getLongDateTime((Integer) time, format);
    }


    public static String getLongDateTime(Integer time, String format) {
        Instant instant = Instant.ofEpochSecond(time);
        return DateTimeFormatter.ofPattern(format)
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }
}

