package com.dthfish.calendar.vertical.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dthfish.calendar.CalendarItem;
import com.dthfish.calendar.R;

import java.util.ArrayList;
import java.util.List;


public class CalendarView extends RelativeLayout {

    private Context mContext;
    private RecyclerView mRvCalendar;
    private GridLayoutManager mGridLayoutManager;
    private CalendarAdapter mAdapter;
    private LinearLayout mLlWeek;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_calendar, this);
        mLlWeek = (LinearLayout) findViewById(R.id.ll_week);
        mRvCalendar = (RecyclerView) findViewById(R.id.rv_calendar);
        mGridLayoutManager = new GridLayoutManager(context, 7);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter.getItem(position).getItemType() == CalendarItem.TYPE_MONTH) {
                    return 7;
                } else {
                    return 1;
                }
            }
        });
        mRvCalendar.setLayoutManager(mGridLayoutManager);
        mAdapter = new CalendarAdapter(context);
        mRvCalendar.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(mListener);
    }

    public void showWeek(boolean show){
        mLlWeek.setVisibility(show?VISIBLE:GONE);
    }

    private SparseArray<CalendarItem> mSelectedItems = new SparseArray<>();
    private CalendarAdapter.OnItemClickListener mListener = new CalendarAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int previousPosition, int currentPosition) {
            CalendarItem preItem = null;
            if (previousPosition != -1) {
                //初始化的时候为-1
                preItem = mAdapter.getItem(previousPosition);
            }
            CalendarItem curItem = mAdapter.getItem(currentPosition);
            if (-1 == previousPosition) {//未选择状态:不存在批量状态
                curItem.isSelected = true;

                mSelectedItems.put(currentPosition, curItem);
                mAdapter.notifyItemChanged(currentPosition);
            } else if (previousPosition == currentPosition) {//两次点击相同:1.已经有批量选择2.没有
                int size = mSelectedItems.size();
                if (size > 1) {
                    clearAllSelected();
                    curItem.isSelected = true;
                    mSelectedItems.put(currentPosition, curItem);
                    mAdapter.notifyItemChanged(currentPosition);
                } else {
                    if (curItem.isSelected) {
                        curItem.isSelected = false;
                        mSelectedItems.remove(currentPosition);
                    } else {
                        curItem.isSelected = true;
                        mSelectedItems.put(currentPosition, curItem);
                    }
                    mAdapter.notifyItemChanged(currentPosition);
                }
            } else if (previousPosition > currentPosition) {//1.上次点击为选中一项，2.上次点击为取消一项

                int size = mSelectedItems.size();
                if (size > 1) {
                    //上一次必定为选中
                    //清除选择，选中当前项目
                    clearAllSelected();
                    curItem.isSelected = true;
                    mSelectedItems.put(currentPosition, curItem);
                    mAdapter.notifyItemChanged(currentPosition);

                } else {
                    preItem.isSelected = false;
                    curItem.isSelected = true;

                    mAdapter.notifyItemChanged(previousPosition);
                    mAdapter.notifyItemChanged(currentPosition);
                }
            } else {//批量选择：1.上次点击为选中一项，2.上次点击为取消一项
                if (preItem.isSelected) {

                    int size = mSelectedItems.size();//必须如此，直接用size()会有问题
                    if (size > 1) {
                        clearAllSelected();
                        curItem.isSelected = true;
                        mSelectedItems.put(currentPosition, curItem);
                        mAdapter.notifyItemChanged(currentPosition);
                    } else {
//                        if (size == 1) {
//                            clearAllSelected();
//                        }
                        //批量选择
                        for (int i = previousPosition; i <= currentPosition; i++) {
                            CalendarItem item = mAdapter.getItem(i);
                            if (item.isSelectable) {
                                item.isSelected = true;
                                mSelectedItems.put(i, item);
                            }
                        }
                        mAdapter.notifyItemRangeChanged(previousPosition, currentPosition - previousPosition + 1);
                    }
                } else {
                    curItem.isSelected = true;
                    mAdapter.notifyItemChanged(currentPosition);
                }
            }
        }
    };

    public List<CalendarItem> getSelectDates(){
        ArrayList<CalendarItem> selectedCalendarItems = new ArrayList<>();
        for (int i = 0; i < mSelectedItems.size(); i++) {
            CalendarItem item = mSelectedItems.valueAt(i);
            if(mAdapter.getSelectedWeekdays().contains(item.getWeekday())){
                selectedCalendarItems.add(item);
            }
        }
        return selectedCalendarItems;
    }

    public void clearAllSelected() {
        for (int i = 0; i < mSelectedItems.size(); i++) {
            CalendarItem item = mSelectedItems.valueAt(i);
            item.isSelected = false;
        }
        //后期可能涉及筛选所以范围尽量大
        int startIndex = mSelectedItems.keyAt(0);
        int index = mSelectedItems.size() - 1;
        if (index < 0) {
            return;
        }
        int endIndex = mSelectedItems.keyAt(index);
        mSelectedItems.clear();
        int itemCount = endIndex - startIndex + 1;
        if (startIndex < 0 || endIndex < 0 || itemCount < 0) {
            return;
        }
        mAdapter.notifyItemRangeChanged(startIndex, itemCount);
    }

    public void setData(List<CalendarItem> data) {
        mAdapter.setData(data);
    }

    public void selectedWeekday(ArrayList<Integer> days){
        mAdapter.setSelectWeekday(days);
    }


}
