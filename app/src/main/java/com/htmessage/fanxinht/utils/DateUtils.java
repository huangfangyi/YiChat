package com.htmessage.fanxinht.utils;

import android.content.Context;
import android.util.Log;

import com.htmessage.fanxinht.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by huangfangyi on 2016/12/24.
 * qq 84543217
 */


public class DateUtils {
    private static final long INTERVAL_IN_MILLISECONDS = 30000L;

    public DateUtils() {
    }

    /**
     * 计算时间差
     *
     * @param starTime
     *            开始时间
     * @param endTime
     *            结束时间
     * @param type
     *            返回类型 ==1----天，时，分。 ==2----时
     * @return 返回时间差
     */
    public static String getTimeDifference(Context context, long starTime, long endTime) {
        String timeString = "";
        try {
            Date parse = new Date(starTime);
            Date parse1 = new Date(endTime);
            long diff = parse1.getTime() - parse.getTime();

            long day = diff / (24 * 60 * 60 * 1000);
            long hour = (diff / (60 * 60 * 1000) - day * 24);
            long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            long ms = (diff - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
                    - min * 60 * 1000 - s * 1000);
            long hour1 = diff / (60 * 60 * 1000);
            long min1 = ((diff / (60 * 1000)) - hour1 * 60);
            if (day == 0){
                if (hour1 == 0){
                    if (0<=min1 && min1<=5){
                        timeString = context.getString(R.string.just);
                    }else if (min1>5 && min1<=15){
                        timeString = context.getString(R.string.just_15);
                    }else if (min1>15 && min1<=30){
                        timeString = context.getString(R.string.just_30);
                    }else{
                        timeString = context.getString(R.string.just_1hour);
                    }
                }else{
                    timeString = hour1+context.getString(R.string.An_hour_ago);
                }
            }else{
                timeString = day+context.getString(R.string.Days_ago);
            }

//            timeString = hour1 + "小时" + min1 + "分";
             Log.d("slj",day + "天" + hour + "小时" + min + "分" + s + "秒");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timeString;

    }


    public static String getTimestampString(Date var0) {
        String var1 = null;
        String var2 = Locale.getDefault().getLanguage();
        boolean var3 = var2.startsWith("zh");
        long var4 = var0.getTime();
        if(isSameDay(var4)) {
            if(var3) {
                var1 = "aa hh:mm";
            } else {
                var1 = "hh:mm aa";
            }
        } else if(isYesterday(var4)) {
            if(!var3) {
                return "Yesterday " + (new SimpleDateFormat("hh:mm aa", Locale.ENGLISH)).format(var0);
            }

            var1 = "昨天aa hh:mm";
        } else if(var3) {
            var1 = "M月d日aa hh:mm";
        } else {
            var1 = "MMM dd hh:mm aa";
        }

        return var3?(new SimpleDateFormat(var1, Locale.CHINESE)).format(var0):(new SimpleDateFormat(var1, Locale.ENGLISH)).format(var0);
    }

    public static boolean isCloseEnough(long var0, long var2) {
        long var4 = var0 - var2;
        if(var4 < 0L) {
            var4 = -var4;
        }

        return var4 < 30000L;
    }

    private static boolean isSameDay(long var0) {
        TimeInfo var2 = getTodayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    private static boolean isYesterday(long var0) {
        TimeInfo var2 = getYesterdayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    public static Date StringToDate(String var0, String var1) {
        SimpleDateFormat var2 = new SimpleDateFormat(var1);
        Date var3 = null;

        try {
            var3 = var2.parse(var0);
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        return var3;
    }

    public static String toTime(int var0) {
        var0 /= 1000;
        int var1 = var0 / 60;
        boolean var2 = false;
        if(var1 >= 60) {
            int var4 = var1 / 60;
            var1 %= 60;
        }

        int var3 = var0 % 60;
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(var1), Integer.valueOf(var3)});
    }

    public static String toTimeBySecond(int var0) {
        int var1 = var0 / 60;
        boolean var2 = false;
        if(var1 >= 60) {
            int var4 = var1 / 60;
            var1 %= 60;
        }

        int var3 = var0 % 60;
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(var1), Integer.valueOf(var3)});
    }


    public static TimeInfo getYesterdayStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.add(Calendar.DAY_OF_MONTH, -1);//5
        var0.set(Calendar.HOUR_OF_DAY, 0);//11
        var0.set(Calendar.MINUTE, 0);//12
        var0.set(Calendar.SECOND, 0);//13
        var0.set(Calendar.MILLISECOND, 0);//Calendar.MILLISECOND
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        Calendar var4 = Calendar.getInstance();
        var4.add(Calendar.DAY_OF_MONTH, -1);//5
        var4.set(Calendar.HOUR_OF_DAY, 23);//11
        var4.set(Calendar.MINUTE, 59);//12
        var4.set(Calendar.SECOND, 59);//13
        var4.set(Calendar.MILLISECOND, 999);//Calendar.MILLISECOND
        Date var5 = var4.getTime();
        long var6 = var5.getTime();
        TimeInfo var8 = new TimeInfo();
        var8.setStartTime(var2);
        var8.setEndTime(var6);
        return var8;
    }

    public static TimeInfo getTodayStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.set(Calendar.HOUR_OF_DAY, 0);
        var0.set(Calendar.MINUTE, 0);
        var0.set(Calendar.SECOND, 0);
        var0.set(Calendar.MILLISECOND, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");
        Calendar var5 = Calendar.getInstance();
        var5.set(Calendar.HOUR_OF_DAY, 23);
        var5.set(Calendar.MINUTE, 59);
        var5.set(Calendar.SECOND, 59);
        var5.set(Calendar.MILLISECOND, 999);
        Date var6 = var5.getTime();
        long var7 = var6.getTime();
        TimeInfo var9 = new TimeInfo();
        var9.setStartTime(var2);
        var9.setEndTime(var7);
        return var9;
    }

    public static TimeInfo getBeforeYesterdayStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.add(Calendar.DAY_OF_MONTH, -2);
        var0.set(Calendar.HOUR_OF_DAY, 0);
        var0.set(Calendar.MINUTE, 0);
        var0.set(Calendar.SECOND, 0);
        var0.set(Calendar.MILLISECOND, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        Calendar var4 = Calendar.getInstance();
        var4.add(Calendar.DAY_OF_MONTH, -2);
        var4.set(Calendar.HOUR_OF_DAY, 23);
        var4.set(Calendar.MINUTE, 59);
        var4.set(Calendar.SECOND, 59);
        var4.set(Calendar.MILLISECOND, 999);
        Date var5 = var4.getTime();
        long var6 = var5.getTime();
        TimeInfo var8 = new TimeInfo();
        var8.setStartTime(var2);
        var8.setEndTime(var6);
        return var8;
    }

    public static TimeInfo getCurrentMonthStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.set(Calendar.DATE, 1);
        var0.set(Calendar.HOUR_OF_DAY, 0);
        var0.set(Calendar.MINUTE, 0);
        var0.set(Calendar.SECOND, 0);
        var0.set(Calendar.MILLISECOND, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        Calendar var4 = Calendar.getInstance();
        Date var5 = var4.getTime();
        long var6 = var5.getTime();
        TimeInfo var8 = new TimeInfo();
        var8.setStartTime(var2);
        var8.setEndTime(var6);
        return var8;
    }

    public static TimeInfo getLastMonthStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.add(Calendar.MONTH, -1);
        var0.set(Calendar.DATE, 1);
        var0.set(Calendar.HOUR_OF_DAY, 0);
        var0.set(Calendar.MINUTE, 0);
        var0.set(Calendar.SECOND, 0);
        var0.set(Calendar.MILLISECOND, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        Calendar var4 = Calendar.getInstance();
        var4.add(Calendar.MONTH, -1);
        var4.set(Calendar.DATE, 1);
        var4.set(Calendar.HOUR_OF_DAY, 23);
        var4.set(Calendar.MINUTE, 59);
        var4.set(Calendar.SECOND, 59);
        var4.set(Calendar.MILLISECOND, 999);
        var4.roll(Calendar.DATE, -1);
        Date var5 = var4.getTime();
        long var6 = var5.getTime();
        TimeInfo var8 = new TimeInfo();
        var8.setStartTime(var2);
        var8.setEndTime(var6);
        return var8;
    }
    public static String getStringTime(long time){
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = sdf.format(date);
        return startTime;
    }
    public static String getTimestampStr() {
        return Long.toString(System.currentTimeMillis());
    }


    public static class TimeInfo {
        private long startTime;
        private long endTime;

        public TimeInfo() {
        }

        public long getStartTime() {
            return this.startTime;
        }

        public void setStartTime(long var1) {
            this.startTime = var1;
        }

        public long getEndTime() {
            return this.endTime;
        }

        public void setEndTime(long var1) {
            this.endTime = var1;
        }
    }

}