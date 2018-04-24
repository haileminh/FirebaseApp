package net.hailm.firebaseapp.utils;


import net.hailm.firebaseapp.define.AppConst;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {
    /**
     * Get day
     *
     * @return
     */

    public static String getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(day);
    }

    /**
     * getMonth
     *
     * @param date
     * @return
     */
    public static String getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH) + 1;
        return String.valueOf(month);
    }

    /**
     * getYear
     *
     * @param date
     * @return
     */
    public static String getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        return String.valueOf(year);
    }

    public static String getTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        SimpleDateFormat format = new SimpleDateFormat(AppConst.FORMAT_HH_MM);
        return format.format(cal.getTime());
    }
}
