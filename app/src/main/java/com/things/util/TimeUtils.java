package com.things.util;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by zhangxiang on 2018/3/4.
 */

public class TimeUtils {
    private static long ucDate;

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日\n HH:mm:ss\n EEEE", Locale.CHINESE);
    private static SimpleDateFormat hourformatter = new SimpleDateFormat("HH:mm:ss", Locale.CHINESE);
    private static SimpleDateFormat weekformatter = new SimpleDateFormat("EEEE", Locale.CHINESE);


    /**
     * 获取当前时间
     */
    public static String getFormatTime() {
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        String time = formatter.format(getInternetTime());
        return time;
    }

    /**
     * 获取当前小时
     */
    public static String getFormatTimeHour() {
        hourformatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        String time = hourformatter.format(getInternetTime());
        return time;
    }

    /**
     * 获取当前的周
     */
    public static String getWeek() {
        hourformatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        String week = weekformatter.format(getInternetTime());
        return week;
    }

    /**
     * 判断当前是否是整点
     */
    public static boolean currentIsTheWholePointOf(Calendar calendar) {
        if ((calendar.get(calendar.MINUTE) == 0) && (calendar.get(calendar.SECOND) == 0)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取互联网时间
     */
    public static Long getInternetTime() {
        URL url = null;//取得资源对象
        try {
            url = new URL("http://www.baidu.com");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            //取得网站日期时间
            ucDate = uc.getDate();
            Utils.myLog(ucDate + "");
        } catch (Exception e) {
            e.printStackTrace();
            Utils.myLog("获取网络时间异常");
        }
        return ucDate;
    }
}
