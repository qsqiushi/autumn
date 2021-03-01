package com.autumn.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.*;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.*;
import java.util.*;

/**
 * 日期工具类
 *
 * @author qiushi
 */
@Slf4j
public class DateUtils {

  public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
  public static final String MMddHHmm = "MM月dd日 HH:mm";
  public static final String yyyyMMdd = "yyyy-MM-dd";
  public static final String yyyyMMddZ = "yyyy-MM-ddZ";
  public static final String yyyyMMddNotSeparate = "yyyyMMdd";
  public static final String yyyyMM = "yyyy-MM";
  public static final String yyyyMMdd000000 = "yyyy-MM-dd 00:00:00";
  public static final String yyyyMMdd235959 = "yyyy-MM-dd 23:59:59";
  public static final String yyyyMMddHH0000 = "yyyy-MM-dd HH:00:00";
  public static final String HHmmss = "HH:mm:ss";
  public static final String HHmm = "HH:mm";
  public static final String yyMMddhhmmss = "yyMMddHHmmss";

  /** @return */
  public static Date newDate() {
    return new Date();
  }

  /**
   * @param date
   * @return
   */
  public static java.sql.Date utilData2sqlDate(Date date) {
    if (Objects.isNull(date)) {
      return null;
    }
    return new java.sql.Date(date.getTime());
  }

  /**
   * @param date
   * @return
   */
  public static Date sqlData2utilDate(java.sql.Date date) {
    if (Objects.isNull(date)) {
      return null;
    }
    return new Date(date.getTime());
  }

  /**
   * UTC时间转换为本时区时间
   *
   * @param date
   * @return
   */
  public static Date utc2Local(String date) {
    DateFormat utcFormat = new SimpleDateFormat(yyyyMMddHHmmss);
    utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    try {
      return utcFormat.parse(date);
    } catch (ParseException e) {
      log.error("时间转换异常", e);
      return null;
    }
  }

  /**
   * Date转换为String
   *
   * @param date
   * @return
   */
  public static String date2Str(Date date) {
    return date2Str(date, yyyyMMddHHmmss);
  }

  /**
   * Date转换为String
   *
   * @param date
   * @param pattern
   * @return
   */
  public static String date2Str(Date date, String pattern) {
    if (date == null) {
      return "";
    }
    return new SimpleDateFormat(pattern).format(date);
  }

  /**
   * 转换日期格式
   *
   * @param date
   * @param pattern
   * @return
   */
  public static Date date2Date(Date date, String pattern) {
    if (date == null) {
      return null;
    }
    return str2Date(date2Str(date, pattern));
  }

  /**
   * String转换为Date
   *
   * @param str
   * @return
   */
  public static Date str2Date(String str) {
    return str2Date(str, yyyyMMddHHmmss);
  }

  /**
   * String转换为Date
   *
   * @param str
   * @return
   */
  public static Date str2Date(String str, String pattern) {
    if (str == null || str.length() == 0) {
      return null;
    }
    try {
      SimpleDateFormat format = new SimpleDateFormat(pattern);
      return format.parse(str);
    } catch (ParseException e) {
      log.error("日期转换异常", e);
      return null;
    }
  }

  /**
   * 秒差
   *
   * @param start
   * @param end
   * @return
   */
  public static int secondsBetween(Date start, Date end) {
    DateTime _start = new DateTime(start);
    DateTime _end = new DateTime(end);
    return Seconds.secondsBetween(_start, _end).getSeconds();
  }

  /**
   * 分钟差
   *
   * @param start
   * @param end
   * @return
   */
  public static int minutesBetween(Date start, Date end) {
    DateTime _start = new DateTime(start);
    DateTime _end = new DateTime(end);
    return Minutes.minutesBetween(_start, _end).getMinutes();
  }

  /**
   * 小时差
   *
   * @param start
   * @param end
   * @return
   */
  public static int hoursBetween(Date start, Date end) {
    DateTime _start = new DateTime(start);
    DateTime _end = new DateTime(end);
    return Hours.hoursBetween(_start, _end).getHours();
  }

  /**
   * 日期间隔
   *
   * @param start
   * @param end
   * @return
   */
  public static int daysBetween(Date start, Date end) {
    DateTime _start = new DateTime(start);
    DateTime _end = new DateTime(end);
    return Days.daysBetween(_start, _end).getDays();
  }

  /**
   * 星期间隔
   *
   * @param start
   * @param end
   * @return
   */
  public static int weeksBetween(Date start, Date end) {
    DateTime _start = new DateTime(start);
    DateTime _end = new DateTime(end);
    return Weeks.weeksBetween(_start, _end).getWeeks();
  }

  /**
   * 月份间隔
   *
   * @param start
   * @param end
   * @return
   */
  public static int monthsBetween(Date start, Date end) {
    DateTime _start = new DateTime(start);
    DateTime _end = new DateTime(end);
    return Months.monthsBetween(_start, _end).getMonths();
  }

  /**
   * 年份间隔
   *
   * @param start
   * @param end
   * @return
   */
  public static int yearsBetween(Date start, Date end) {
    DateTime _start = new DateTime(start);
    DateTime _end = new DateTime(end);
    return Years.yearsBetween(_start, _end).getYears();
  }

  /**
   * 秒加减
   *
   * @param start
   * @param seconds
   * @return
   */
  public static Date secondsAfter(Date start, int seconds) {
    DateTime _start = new DateTime(start);
    DateTime _end = _start.plusSeconds(seconds);
    return _end.toDate();
  }

  /**
   * 分钟加减
   *
   * @param start
   * @param minitues
   * @return
   */
  public static Date minituesAfter(Date start, int minitues) {
    DateTime _start = new DateTime(start);
    DateTime _end = _start.plusMinutes(minitues);
    return _end.toDate();
  }

  /**
   * 小时加减
   *
   * @param start
   * @param hours
   * @return
   */
  public static Date hoursAfter(Date start, int hours) {
    DateTime _start = new DateTime(start);
    DateTime _end = _start.plusHours(hours);
    return _end.toDate();
  }

  /**
   * 日期加减
   *
   * @param start
   * @param days
   * @return
   */
  public static Date daysAfter(Date start, int days) {
    DateTime _start = new DateTime(start);
    DateTime _end = _start.plusDays(days);
    return _end.toDate();
  }

  /**
   * 星期加减
   *
   * @param start
   * @param weeks
   * @return
   */
  public static Date weeksAfter(Date start, int weeks) {
    DateTime _start = new DateTime(start);
    DateTime _end = _start.plusWeeks(weeks);
    return _end.toDate();
  }

  /**
   * 日期加减
   *
   * @param start
   * @param months
   * @return
   */
  public static Date monthsAfter(Date start, int months) {
    DateTime _start = new DateTime(start);
    DateTime _end = _start.plusMonths(months);
    return _end.toDate();
  }

  /**
   * 年份加减
   *
   * @param start
   * @param years
   * @return
   */
  public static Date yeadAfter(Date start, int years) {
    DateTime _start = new DateTime(start);
    DateTime _end = _start.plusYears(years);
    return _end.toDate();
  }

  /**
   * 获取每天的开始时间
   *
   * @param date
   * @return
   */
  public static Date getStartOfDate(Date date) {
    if (date == null) {
      date = new Date();
    }
    DateTime _start = new DateTime(date);
    return str2Date(_start.toString(yyyyMMdd000000), yyyyMMddHHmmss);
  }

  /**
   * 获取每天的结束时间
   *
   * @param date
   * @return
   */
  public static Date getEndOfDate(Date date) {
    if (date == null) {
      date = new Date();
    }
    DateTime _start = new DateTime(date);
    return str2Date(_start.toString(yyyyMMdd235959), yyyyMMddHHmmss);
  }

  /**
   * 获得一年中的第几周
   *
   * @return 一年中的第几周
   */
  public static int currentWeekOfYear(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.WEEK_OF_YEAR);
  }

  /** 获得当前年份 */
  public static int currentYear() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    return calendar.get(Calendar.YEAR);
  }

  /**
   * 是否是同一天
   *
   * @param date1
   * @param date2
   * @return
   */
  public static boolean isSameDate(Date date1, Date date2) {
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(date1);
    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(date2);
    boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    boolean isSameMonth = isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    return isSameMonth && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * 获得明天
   *
   * @return
   */
  public static Date getTomorrow() {
    return addDay(1);
  }

  /**
   * 后天
   *
   * @return
   */
  public static Date getTheDayAfterTomorrow() {
    return addDay(2);
  }

  /**
   * 添加天
   *
   * @param num
   * @return
   */
  public static Date addDay(int num) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DATE, num);
    return calendar.getTime();
  }

  /**
   * 获得当前小时
   *
   * @return
   */
  public static int getCurrentHour() {
    return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
  }

  /**
   * 获取UTC时间
   *
   * @param pattern
   * @return
   */
  public static String getUTCTimeStr(String pattern) {
    // 1、取得本地时间：
    Calendar cal = Calendar.getInstance();
    // 2、取得时间偏移量：
    int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
    // 3、取得夏令时差：
    int dstOffset = cal.get(Calendar.DST_OFFSET);
    // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
    cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
    int year = cal.get(Calendar.YEAR);
    // int month = cal.get(Calendar.MONTH)+1;
    int month = cal.get(Calendar.MONTH);
    int day = cal.get(Calendar.DAY_OF_MONTH);
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    int minute = cal.get(Calendar.MINUTE);
    int second = cal.get(Calendar.SECOND);

    cal.set(year, month, day, hour, minute, second);
    Date utcDate = cal.getTime();
    return DateUtils.date2Str(utcDate, pattern);
  }

  // 保证时间格式正确，个位数的用0补位，否则在DateTimeFormatter中parse会出错
  public static String checkFormat(int number) {

    DecimalFormat df = new DecimalFormat("00");
    String formatStr = df.format(number);

    return formatStr;
  }

  /**
   * 日期格式字符串转换成unix时间戳
   *
   * @param date
   * @return
   */
  public static String Date2TimeStamp(Date date) {
    try {
      return String.valueOf(date.getTime() / 1000);
    } catch (Exception e) {
      log.error("日期格式转换异常", e);
    }
    return "";
  }

  /**
   * Java将Unix时间戳转换成指定格式日期字符串
   *
   * @param timestampString 时间戳 如："1473048265";
   * @param formats 要格式化的格式 默认："yyyy-MM-dd HH:mm:ss";
   * @return 返回结果 如："2016-09-05 16:06:42";
   */
  public static String TimeStamp2Date(String timestampString, String formats) {
    if (StringUtils.isEmpty(formats)) {
      return null;
    }

    Long timestamp = Long.parseLong(timestampString) * 1000;
    String date =
        new SimpleDateFormat(DateUtils.yyyyMMddHHmmss, Locale.CHINA).format(new Date(timestamp));
    return date;
  }

  public static Date asDate(LocalDate localDate) {
    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }

  public static Date asDate(LocalDateTime localDateTime) {
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static LocalDate asLocalDate(Date date) {
    return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
  }

  public static LocalDateTime asLocalDateTime(Date date) {
    return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  /**
   * 获得指定年份的总天数
   *
   * @param year 年份
   * @return 天
   * @since 5.3.6
   */
  public static int lengthOfYear(int year) {
    return Year.of(year).length();
  }

  /**
   * 获得指定月份的总天数
   *
   * @param month 年份
   * @param isLeapYear 是否闰年
   * @return 天
   * @since 5.4.2
   */
  public static int lengthOfMonth(int month, boolean isLeapYear) {
    return Month.of(month).length(isLeapYear);
  }
}
