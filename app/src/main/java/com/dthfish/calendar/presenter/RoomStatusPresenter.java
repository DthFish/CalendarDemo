package com.dthfish.calendar.presenter;

import android.content.Context;

import com.dthfish.calendar.view.IRoomStatusView;
import com.dthfish.calendar.widget.CalendarItem;
import com.dthfish.calendar.widget.CalendarUtils;

import java.util.Calendar;


public class RoomStatusPresenter {
    private Context mContext;
    private IRoomStatusView mView;
    public RoomStatusPresenter(Context context, IRoomStatusView view) {
        mContext = context;
        mView = view;
    }

    public void start(){
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.MONTH,3);

        Calendar today = Calendar.getInstance();
        CalendarUtils.setToDayBeginning(today);
        final long dayTime = today.getTimeInMillis();

        mView.setCalendarData(CalendarUtils.getDateRange(startCalendar, endCalendar, new CalendarUtils.OnCreateDailyCalendarItem() {
            @Override
            public void handleItem(CalendarItem item, long time) {
                /*if(item.getWeekday() == 1 || item.getWeekday() == 7){
                    item.isSelectable = false;
                }*/
                if(time < dayTime){
                    item.isSelectable = false;
                }
            }
        }));
    }
}
