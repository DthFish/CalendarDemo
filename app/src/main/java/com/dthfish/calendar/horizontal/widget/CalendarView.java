package com.dthfish.calendar.horizontal.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dthfish.calendar.CalendarItem;
import com.dthfish.calendar.R;


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
        LayoutInflater.from(context).inflate(R.layout.view_horizontal_calendar, this);
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
//        mAdapter = new CalendarAdapter(context);
//        mRvCalendar.setAdapter(mAdapter);
//        mAdapter.setOnItemClickListener(mListener);
    }

    public void showWeek(boolean show) {
        mLlWeek.setVisibility(show ? VISIBLE : GONE);
    }

    /*private CalendarAdapter.OnItemClickListener mListener = new CalendarAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(*//*int previousPosition,*//* int currentPosition,int adapterIndex) {
            CalendarItem preItem = null;
            if (previousPosition != -1) {
                //初始化的时候为-1
                preItem = mAdapter.getItem(previousPosition);
            }
            CalendarItem curItem = mAdapter.getItem(currentPosition);
            if (-1 == previousPosition) {//未选择状态:不存在批量状态
                curItem.isSelected = true;

                mAdapter.putSelectedItem(currentPosition, curItem);
                mAdapter.notifyItemChanged(currentPosition);
            } else if (previousPosition == currentPosition) {//两次点击相同:1.已经有批量选择2.没有
                int size = mAdapter.selectedCount();
                if (size > 1) {
                    mAdapter.clearAllSelected();
                    curItem.isSelected = true;
                    mAdapter.putSelectedItem(currentPosition, curItem);
                    mAdapter.notifyItemChanged(currentPosition);
                } else {
                    if (curItem.isSelected) {
                        curItem.isSelected = false;
                        mAdapter.removeSelectedItem(currentPosition);
                    } else {
                        curItem.isSelected = true;
                        mAdapter.putSelectedItem(currentPosition, curItem);
                    }
                    mAdapter.notifyItemChanged(currentPosition);
                }
            } else if (previousPosition > currentPosition) {//1.上次点击为选中一项，2.上次点击为取消一项

                int size = mAdapter.selectedCount();
                if (size > 1) {
                    //上一次必定为选中
                    //清除选择，选中当前项目
                    mAdapter.clearAllSelected();
                    curItem.isSelected = true;
                    mAdapter.putSelectedItem(currentPosition, curItem);
                    mAdapter.notifyItemChanged(currentPosition);

                } else {
                    preItem.isSelected = false;
                    curItem.isSelected = true;

                    mAdapter.notifyItemChanged(previousPosition);
                    mAdapter.notifyItemChanged(currentPosition);
                }
            } else {//批量选择：1.上次点击为选中一项，2.上次点击为取消一项
                if (preItem.isSelected) {

                    int size = mAdapter.selectedCount();//必须如此，直接用size()会有问题
                    if (size > 1) {
                        mAdapter.clearAllSelected();
                        curItem.isSelected = true;
                        mAdapter.putSelectedItem(currentPosition, curItem);
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
                                mAdapter.putSelectedItem(i, item);
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
    };*/

    public void setAdapter(CalendarAdapter adapter) {
        mAdapter = adapter;
        mRvCalendar.setAdapter(mAdapter);
    }

}
