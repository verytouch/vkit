package com.verytouch.vkit.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author verytouch
 * @since 2021/5/13 21:13
 */
public class DateUtils {

    public static final DateTimeFormatter FORMATTER_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final ZoneId ZONE_ID = ZoneId.systemDefault();
    public static final ZoneOffset ZONE_OFFSET = ZONE_ID.getRules().getOffset(Instant.now());
    /**
     * LocalDateTime转Date
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZONE_ID).toInstant());
    }

    /**
     * Date转LocalDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZONE_ID);
    }

    /**
     * 返回当前时间，格式yyyy-MM-dd HH:mm:ss
     */
    public static String nowDateTime() {
        return LocalDateTime.now().format(FORMATTER_DATE_TIME);
    }

    /**
     * 返回周岁
     */
    public static int ageOf(LocalDate birthday) {
        return birthday.until(LocalDate.now()).getYears();
    }
}
