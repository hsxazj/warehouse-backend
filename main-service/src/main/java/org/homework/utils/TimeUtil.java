package org.homework.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zhanghaifeng
 */
public class TimeUtil {

    public static final long ONE_SECOND_TIME = 1000;
    public static final long ONE_MINUTE_TIME = 60 * ONE_SECOND_TIME;
    public static final long ONE_HOUR_TIME = 60 * ONE_MINUTE_TIME;
    public static final long ONE_DAY_TIME = 24 * ONE_HOUR_TIME;

    public static final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");

    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取毫秒值
     *
     * @param date yyyy-MM-dd格式的字符串
     * @return 毫秒值
     */
    public static long getDateMillis(String date) throws ParseException {
        return dateFormat.parse(date).getTime();
    }

    /**
     * 获取毫秒值  时间格式为 HH:mm:ss
     *
     * @param time HH:mm:ss格式的字符串
     * @return 毫秒值
     */
    public static long getTimeMillis(String time) throws ParseException {
        return timeFormat.parse(time).getTime();
    }

    /**
     * 获取日期时间的毫秒值
     *
     * @param dateTime yyyy-MM-dd HH:mm:ss格式的字符串
     * @return 毫秒值
     */
    public static long getDateTimeMillis(String dateTime) throws ParseException {
        return dateTimeFormat.parse(dateTime).getTime();
    }

    /**
     * 获取毫秒值
     *
     * @param dateMonth yyyy-MM 格式的字符串
     * @return 毫秒值
     */
    public static long getMonthMillis(String dateMonth) throws ParseException {
        return monthFormat.parse(dateMonth).getTime();
    }


    /**
     * 获取当日0点的时间毫秒值
     *
     * @return 毫秒值
     */
    public static long getTodayZero() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取从当日0点开始，n天之后的毫秒值
     *
     * @param day n天
     * @return 毫秒值
     */
    public static long getDaysLaterFromTodayZero(int day) {
        long todayZero = getTodayZero();
        return todayZero + ONE_DAY_TIME + day * ONE_DAY_TIME;
    }

    /**
     * 获取n天后的毫秒值
     *
     * @param day n天
     * @return 毫秒值
     */
    public static long getDaysLater(int day) {
        Date now = new Date();
        return now.getTime() + day * ONE_DAY_TIME;
    }

    /**
     * 将Date对象转换为字符串
     *
     * @param date date对象
     * @return HH:mm:ss格式的字符串
     */
    public static String getTimeString(Date date) {
        return timeFormat.format(date);
    }

    /**
     * 将Date对象转换为字符串
     *
     * @param date date对象
     * @return yyyy-MM-dd 格式的字符串
     */
    public static String getDateString(Date date) {
        return dateFormat.format(date);
    }

    /**
     * 将Date对象转换为字符串
     *
     * @param date date对象
     * @return yyyy-MM-dd HH:mm:ss 格式的字符串
     */
    public static String getDateTimeString(Date date) {
        return dateTimeFormat.format(date);
    }


}
