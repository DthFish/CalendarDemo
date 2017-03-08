package com.dthfish.calendar.vertical.widget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

/**
 * Utilities for Calendar
 */
public class CalendarUtils {

    /**
     * @param date {@linkplain Date} to pull date information from
     * @return a new Calendar instance with the date set to the provided date. Time set to zero.
     */
    public static Calendar getInstance(@Nullable Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        copyDateTo(calendar, calendar);
        return calendar;
    }

    /**
     * @return a new Calendar instance with the date set to today. Time set to zero.
     */
    @NonNull
    public static Calendar getInstance() {
        Calendar calendar = Calendar.getInstance();
        copyDateTo(calendar, calendar);
        return calendar;
    }

    /**
     * Set the provided calendar to the first day of the month. Also clears all time information.
     *
     * @param calendar {@linkplain Calendar} to modify to be at the first fay of the month
     */
    public static void setToFirstDay(Calendar calendar) {
        int year = getYear(calendar);
        int month = getMonth(calendar);
        calendar.clear();
        calendar.set(year, month, 1);
    }

    public static void setToDayBeginning(Calendar calendar){
        int year = getYear(calendar);
        int month = getMonth(calendar);
        int day = getDay(calendar);
        calendar.clear();
        calendar.set(year, month, day);
    }

    /**
     * Copy <i>only</i> date information to a new calendar.
     *
     * @param from calendar to copy from
     * @param to   calendar to copy to
     */
    public static void copyDateTo(Calendar from, Calendar to) {
        int year = getYear(from);
        int month = getMonth(from);
        int day = getDay(from);
        to.clear();
        to.set(year, month, day);
    }

    public static int getYear(Calendar calendar) {
        return calendar.get(YEAR);
    }

    public static int getMonth(Calendar calendar) {
        return calendar.get(MONTH);
    }

    public static int getDay(Calendar calendar) {
        return calendar.get(DATE);
    }

    public static int getDayOfWeek(Calendar calendar) {
        return calendar.get(DAY_OF_WEEK);
    }

    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return format.format(new Date(time));
    }

    /**
     * {@link com.android.gift.ebooking.product.view.RoomStatusActivity}用于获取日历数据
     * @param startCalendar
     * @param endCalendar
     * @return
     */

    public static List<CalendarItem> getDateRange(Calendar startCalendar, Calendar endCalendar, OnCreateDailyCalendarItem handle) {
        if (startCalendar == null || endCalendar == null) {
            throw new IllegalArgumentException("arguments should not be null!");
        }
        if (startCalendar.after(endCalendar)) {
            throw new IllegalArgumentException("Start after end!");
        }

        if(startCalendar == endCalendar){
            throw new IllegalArgumentException("Start and end can not be the same object!");
        }
        setToFirstDay(startCalendar);
        setToFirstDay(endCalendar);
        ArrayList<CalendarItem> calendarItems = new ArrayList<>();

        for (; !startCalendar.after(endCalendar); startCalendar.add(MONTH, 1)) {
            //添加月份标题
            calendarItems.add(new CalendarItem(CalendarItem.TYPE_MONTH, getTime(startCalendar.getTimeInMillis())));
            //添加空白
            int startDayOfWeek = startCalendar.get(DAY_OF_WEEK);
            for (int i = 1; i < startDayOfWeek; i++) {
                calendarItems.add(new CalendarItem(CalendarItem.TYPE_SPACE, ""));
            }
            //添加每月日期
            int actualMaximum = startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 0; i < actualMaximum; i++) {
                CalendarItem item = new CalendarItem(CalendarItem.TYPE_DAY, getTime(startCalendar.getTimeInMillis()), startCalendar.get(DAY_OF_WEEK));
                if(handle != null){
                    handle.handleItem(item,startCalendar.getTimeInMillis());
                }
                calendarItems.add(item);
                Log.d("time:", "time: " + startCalendar.getTimeInMillis());
                startCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            startCalendar.add(Calendar.DAY_OF_MONTH, -1);
            int EndDayOfWeek = startCalendar.get(DAY_OF_WEEK);

            for (int i = 0; i < 7 - EndDayOfWeek; i++) {
                calendarItems.add(new CalendarItem(CalendarItem.TYPE_SPACE, ""));
            }
            //设置为每月1号
            setToFirstDay(startCalendar);
        }

        return calendarItems;
    }

    public interface OnCreateDailyCalendarItem {
        /**
         * 避免直接修改item 的 date
         * @param item
         * @param time
         */
        void handleItem(CalendarItem item, long time);
    }
}
