package com.telemarket.telemarketer.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Be careful.
 * Author: Hanson
 * Email: imyijie@outlook.com
 * Date: 2016/12/1
 */
public class TimeUtils {
    private static final DateTimeFormatter RFC822 = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    public static ZonedDateTime parseRFC822(String timeStr) {
        return ZonedDateTime.parse(timeStr, RFC822);
    }

    public static String toRFC822(ZonedDateTime dateTime) {
        return dateTime.format(RFC822);
    }
}
