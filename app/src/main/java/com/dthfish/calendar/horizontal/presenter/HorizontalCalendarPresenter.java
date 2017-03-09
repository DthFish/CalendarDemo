package com.dthfish.calendar.horizontal.presenter;

import android.content.Context;

import com.dthfish.calendar.horizontal.view.IHorizontalView;
import com.dthfish.calendar.CalendarItem;
import com.dthfish.calendar.CalendarUtils;

import java.util.Calendar;


/**
 * Description ${Desc}
 * Author Zhaolizhi
 * Date 2017/3/9.
 */

public class HorizontalCalendarPresenter {
    private Context mContext;
    private IHorizontalView mView;
    public HorizontalCalendarPresenter(Context context,IHorizontalView view) {
        mContext = context;
        mView = view;
    }

    /**
     *
     * @param startYear
     * @param startMonth 月份最大为11，最小为0
     * @param endYear
     * @param endMonth  月份最大为11，最小为0
     * @return
     */
    public void getCalendarData(int startYear, int startMonth, int endYear, int endMonth){
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.clear();
        startCalendar.set(startYear,startMonth,1);


        Calendar endCalendar = Calendar.getInstance();
        endCalendar.clear();
        endCalendar.set(endYear,endMonth,1);

        Calendar today = Calendar.getInstance();
        CalendarUtils.setToDayBeginning(today);
        final long dayTime = today.getTimeInMillis();


        CalendarUtils.OnCreateDailyCalendarItem handleItem = new CalendarUtils.OnCreateDailyCalendarItem() {
            @Override
            public void handleItem(CalendarItem item, long time) {
                /*if(item.getWeekday() == 1 || item.getWeekday() == 7){
                    item.isSelectable = false;
                }*/
                if (time < dayTime) {
                    item.isSelectable = false;
                }
            }
        };

        mView.initData(CalendarUtils.getMonthRange(startCalendar,endCalendar,handleItem));
    }
}
