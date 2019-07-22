package com.hanwin.product.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * TimeUtils
 */
public class TimeUtils {

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    public static final SimpleDateFormat DATE_YEAR_MONTH_DAY = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy年MM月dd HH:mm");
    public static final SimpleDateFormat DATE_FORMAT_MONTH_DAY = new SimpleDateFormat("MM月dd日");

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    public static Date stringToDate(String dateStr) {

        Date date = null;
        try {
            date = DATE_FORMAT_DATE.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateToString(Date date) {
        return DATE_FORMAT_DATE.format(date);
    }

    /**
     * 将时间转为时间戳
     *
     * @param datestr
     * @return
     * @throws ParseException
     */
    public static long dateToLong(String datestr) throws ParseException {
        Date date = DEFAULT_DATE_FORMAT.parse(datestr);
        long ts = date.getTime();
        return ts;
    }

    /**
     * 获取当前时区的系统时间
     *
     * @return
     * @throws Exception
     */
    public static long getCurrentTimeMillis() throws Exception {
        TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8");
        TimeZone.setDefault(tz);
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 可以方便地修改日期格式
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));//设置当地时间
        String timeStr = dateFormat.format(now);
        Date date = dateFormat.parse(timeStr);
        long time = date.getTime();
        return time;
    }

    /**
     * 获取当前时区的系统时间 yyyy-MM-dd HH:mm:ss
     *
     * @return
     * @throws Exception
     */
    public static String getCurrentTime() throws Exception {
        TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8");
        TimeZone.setDefault(tz);
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");// 可以方便地修改日期格式
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));//设置当地时间
        String timeStr = dateFormat.format(now);
        return timeStr;
    }

    /**
     * 将短时间格式时间转换为字符串 yyyy年MM月dd日
     */
    public static String dateToStr(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s, SimpleDateFormat sdf) {
        String res = "";
        if (!TextUtils.isEmpty(s)) {
            long lt = new Long(s);
            Date date = new Date(lt);
            res = sdf.format(date);
        }
        return res;
    }

    /**
     * 获取年月日
     *
     * @return
     * @throws Exception
     */
    public static String getYearMonthDay() throws Exception {
        TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8");
        TimeZone.setDefault(tz);
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期格式
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));//设置当地时间
        String timeStr = dateFormat.format(now);
        return timeStr;
    }

    /**
     * 获取指定时间是本年第几周
     * @return
     */
    public static int getWeek() {
        Calendar cal = Calendar.getInstance();//这一句必须要设置，否则美国认为第一天是周日，而我国认为是周一，对计算当期日期是第几周会有错误
        cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 每周从周一开始
        cal.setMinimalDaysInFirstWeek(7); // 设置每周最少为7天
        cal.setTime(new Date());
        int weeks = cal.get(Calendar.WEEK_OF_YEAR);
        return weeks;
    }
}
