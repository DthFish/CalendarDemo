package com.dthfish.calendar.horizontal.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dthfish.calendar.R;
import com.dthfish.calendar.CalendarItem;
import com.dthfish.calendar.horizontal.widget.CalendarAdapter;
import com.dthfish.calendar.horizontal.widget.CalendarView;

import java.util.ArrayList;
import java.util.List;


/**
 * Description ${Desc}
 * Author Zhaolizhi
 * Date 2017/3/9.
 */

public class MonthAdapter extends PagerAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<List<CalendarItem>> mLists = new ArrayList<>();
    private SparseArray<View> mViews = new SparseArray<>();
    private SparseArray<CalendarView> mCalendarViews = new SparseArray<>();
    private SparseArray<CalendarAdapter> mRvAdapters = new SparseArray<>();
    public MonthAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public List<List<CalendarItem>> getLists() {
        return mLists;
    }

    public void setLists(List<List<CalendarItem>> lists) {
        mLists.clear();
        mLists.addAll(lists);
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        boolean isNew = false;
        View view = mViews.get(position);
        if (view == null) {
            isNew = true;
            view = mLayoutInflater.inflate(R.layout.item_calendar_vp, container, false);
        }
        CalendarView calendarView = (CalendarView) view.findViewById(R.id.cv);
        calendarView.showWeek(false);
        if (isNew) {
//            calendarView.setData(mLists.get(position));
            CalendarAdapter adapter = new CalendarAdapter(mContext);
            adapter.setData(mLists.get(position));
            adapter.setSelectWeekday(mSelectedWeekdays);
//            calendarView.selectedWeekday(mSelectedWeekdays);
            calendarView.setAdapter(adapter);
            mRvAdapters.put(position,adapter);
            mViews.put(position, view);
            mCalendarViews.put(position,calendarView);
        }
        container.addView(view);

        return view;
    }

    public List<CalendarItem> getSelectedDatas(){
        //TODO: return
        List<CalendarItem> selectedData =new ArrayList<>();
        for (int i = 0,size = mRvAdapters.size(); i < size; i++) {
            CalendarAdapter calendarAdapter = mRvAdapters.valueAt(i);
            selectedData.addAll(calendarAdapter.getSelectData());
        }
        return selectedData;

    }
    ArrayList<Integer> mSelectedWeekdays = new ArrayList<>();
    public void setSelectWeekday(ArrayList<Integer> days) {
        mSelectedWeekdays.clear();
        mSelectedWeekdays.addAll(days);
        for (int i = 0,size = mRvAdapters.size(); i < size; i++) {
            mRvAdapters.valueAt(i).setSelectWeekday(days);
        }
    }

    public void clearAllSelected(){
        for (int i = 0,size = mRvAdapters.size(); i < size; i++) {
            CalendarAdapter calendarAdapter = mRvAdapters.valueAt(i);
            calendarAdapter.clearAllSelected();
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
