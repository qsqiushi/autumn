package com.autumn.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Locale;

/** @author 邱实 JDK8时间类型帮助类 */
public class LocalDateTimeUtils {

  public static final ZoneId BEIJING_ZONE_ID = ZoneId.of("GMT+08:00");

  public static final String P_yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
  public static final String P_yyyyMMdd = "yyyy-MM-dd";
  public static final String P_yyyyMM = "yyyy-MM";
  public static final String P_HHmmss = "HH:mm:ss";
  public static final String P_yyMMddHHmmss__NotSeparate = "yyMMddHHmmss";
  public static final String P_yyyyMMdd_NotSeparate = "yyyyMMdd";
  public static final String P_yyyyMMdd_ZN = "yyyy年MM月dd日";

  /** Tue Nov 21 14:48:09 2017 */
  public static DateTimeFormatter yyyyMMddHHmmss =
      DateTimeFormatter.ofPattern(P_yyyyMMddHHmmss, Locale.SIMPLIFIED_CHINESE);

  public static DateTimeFormatter yyyyMMdd =
      DateTimeFormatter.ofPattern(P_yyyyMMdd, Locale.SIMPLIFIED_CHINESE);
  public static DateTimeFormatter yyyyMM =
      DateTimeFormatter.ofPattern(P_yyyyMM, Locale.SIMPLIFIED_CHINESE);
  public static DateTimeFormatter HHmmss =
      DateTimeFormatter.ofPattern(P_HHmmss, Locale.SIMPLIFIED_CHINESE);
  public static DateTimeFormatter P_yyMMddhhmmss__NotSeparate =
      DateTimeFormatter.ofPattern(P_yyMMddHHmmss__NotSeparate, Locale.SIMPLIFIED_CHINESE);
  public static DateTimeFormatter yyyyMMdd_NotSeparate =
      DateTimeFormatter.ofPattern(P_yyyyMMdd_NotSeparate, Locale.SIMPLIFIED_CHINESE);
  public static DateTimeFormatter yyyyMMdd_ZN =
      DateTimeFormatter.ofPattern(P_yyyyMMdd_ZN, Locale.SIMPLIFIED_CHINESE);

  /**
   * Date转换为LocalDateTime
   *
   * @param date date类型时间
   * @return 返回Date转换为LocalDateTime
   */
  public static LocalDateTime convertDateToLDT(Date date) {
    return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
  }

  /**
   * LocalDateTime转换为Date
   *
   * @param time
   * @return
   */
  public static Date convertLDTToDate(LocalDateTime time) {
    return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
  }

  /**
   * 获取指定日期的毫秒
   *
   * @param time
   * @return
   */
  public static Long getMilliByTime(LocalDateTime time) {
    return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

  /**
   * 获取指定日期的秒
   *
   * @param time
   * @return
   */
  public static Long getSecondsByTime(LocalDateTime time) {
    return time.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
  }

  /**
   * 获取指定时间的指定格式，默认yyyy-MM-dd HH:mm:ss
   *
   * @param time
   * @return
   */
  public static String formatTime(LocalDateTime time) {
    return formatTime(time, P_yyyyMMddHHmmss);
  }

  /**
   * 获取指定时间的指定格式
   *
   * @param time
   * @param pattern
   * @return
   */
  public static String formatTime(LocalDateTime time, String pattern) {
    if (time == null) {
      return null;
    }
    return time.format(DateTimeFormatter.ofPattern(pattern));
  }

  /**
   * 获取当前时间的指定格式
   *
   * @param pattern
   * @return
   */
  public static String formatNow(String pattern) {
    return formatTime(LocalDateTime.now(), pattern);
  }

  /**
   * 日期加上一个数,根据field不同加不同值,field为ChronoUnit.*
   *
   * @param time
   * @param number
   * @param field
   * @return
   */
  public static LocalDateTime plus(LocalDateTime time, long number, TemporalUnit field) {
    return time.plus(number, field);
  }

  /**
   * 日期减去一个数,根据field不同减不同值,field参数为ChronoUnit.*
   *
   * @param time
   * @param number
   * @param field
   * @return
   */
  public static LocalDateTime minu(LocalDateTime time, long number, TemporalUnit field) {
    return time.minus(number, field);
  }

  /**
   * 获取两个日期的差 field参数为ChronoUnit.*
   *
   * @param startTime
   * @param endTime
   * @param field 单位(年月日时分秒)
   * @return
   */
  public static long betweenTwoTime(
      LocalDateTime startTime, LocalDateTime endTime, ChronoUnit field) {
    Period period = Period.between(LocalDate.from(startTime), LocalDate.from(endTime));
    if (field == ChronoUnit.YEARS) {
      return period.getYears();
    }
    if (field == ChronoUnit.MONTHS) {
      return period.getYears() * 12 + period.getMonths();
    }
    return field.between(startTime, endTime);
  }

  /**
   * 获取一天的开始时间，2017,7,22 00:00
   *
   * @param time
   * @return
   */
  public static LocalDateTime getDayStart(LocalDateTime time) {
    return time.withHour(0).withMinute(0).withSecond(0).withNano(0);
  }

  /**
   * 获取一天的结束时间，2017,7,22 23:59:59.999999999
   *
   * @param time
   * @return
   */
  public static LocalDateTime getDayEnd(LocalDateTime time) {
    return time.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
  }

  /**
   * long to LocalDateTime
   *
   * @param timestamp
   * @return
   */
  public static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
    Instant instant = Instant.ofEpochMilli(timestamp);
    ZoneId zone = ZoneId.systemDefault();
    return LocalDateTime.ofInstant(instant, zone);
  }
}
