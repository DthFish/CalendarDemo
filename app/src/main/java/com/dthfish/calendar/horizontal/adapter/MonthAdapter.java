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
            CalendarAdapter adapter = new CalendarAdapter(mContext, position);
            adapter.setData(mLists.get(position));
            adapter.setSelectWeekday(mSelectedWeekdays);
            adapter.setOnItemClickListener(mOnItemClickListener);
            calendarView.setAdapter(adapter);
            mRvAdapters.put(position, adapter);
            mViews.put(position, view);
            mCalendarViews.put(position, calendarView);
        }
        container.addView(view);

        return view;
    }

    private int mPreClickedAdapterIndex = -1;
    private int mPreClickedPosition = -1;
    private CalendarAdapter.OnItemClickListener mOnItemClickListener = new CalendarAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(/*int previousPosition,*/ int currentPosition, int adapterIndex) {
            CalendarAdapter preAdapter = null;
            CalendarItem preItem = null;
            if (mPreClickedAdapterIndex != -1 && mPreClickedPosition != -1) {
                preAdapter = mRvAdapters.get(mPreClickedAdapterIndex);
                preItem = preAdapter.getItem(mPreClickedPosition);
            }

            CalendarAdapter curAdapter = mRvAdapters.get(adapterIndex);
            CalendarItem curItem = curAdapter.getItem(currentPosition);

            //未点击时候,不用判断mPreClickedAdapterIndex
            if (/*mPreClickedAdapterIndex == -1 && */mPreClickedPosition == -1) {
                curItem.isSelected = true;
                curAdapter.putSelectedItem(currentPosition, curItem);
                curAdapter.notifyItemChanged(currentPosition);

            } else if (mPreClickedAdapterIndex == adapterIndex && mPreClickedPosition == currentPosition) {
                //当前页操作,两次选择一致
                int size = getSelectedCount();
                //1.已经有批量选择
                if (size > 1) {
                    clearAllSelected();
                    curItem.isSelected = true;
                    curAdapter.putSelectedItem(currentPosition, curItem);
                    curAdapter.notifyItemChanged(currentPosition);
                } else {
                    // 2.没有
                    if (curItem.isSelected) {
                        curItem.isSelected = false;
                        curAdapter.removeSelectedItem(currentPosition);
                    } else {
                        curItem.isSelected = true;
                        curAdapter.putSelectedItem(currentPosition, curItem);
                    }
                    curAdapter.notifyItemChanged(currentPosition);
                }


            } else if (mPreClickedAdapterIndex == adapterIndex && mPreClickedPosition > currentPosition) {
                //当前页操作,两次选择不一致
                int size = getSelectedCount();
                if (size > 1) {
                    //上次点击为选中
                    clearAllSelected();
                    curItem.isSelected = true;
                    curAdapter.putSelectedItem(currentPosition, curItem);
                    curAdapter.notifyItemChanged(currentPosition);
                } else {
                    preItem.isSelected = false;
                    curItem.isSelected = true;
                    preAdapter.removeSelectedItem(mPreClickedPosition);
                    preAdapter.notifyItemChanged(mPreClickedPosition);
                    curAdapter.putSelectedItem(currentPosition, curItem);
                    curAdapter.notifyItemChanged(currentPosition);
                }

            } else if (mPreClickedAdapterIndex == adapterIndex && mPreClickedPosition < currentPosition) {
                //当前页操作,两次选择不一致
                //批量选择：1.上次点击为选中一项，2.上次点击为取消一项
                if (preItem.isSelected) {
                    int size = getSelectedCount();
                    if (size > 1) {
                        //上次点击为选中
                        curAdapter.clearAllSelected();
                        curItem.isSelected = true;
                        curAdapter.putSelectedItem(currentPosition, curItem);
                        curAdapter.notifyItemChanged(currentPosition);
                    } else {
                        //批量选择
                        for (int i = mPreClickedPosition; i <= currentPosition; i++) {
                            CalendarItem item = curAdapter.getItem(i);
                            if (item.isSelectable) {
                                item.isSelected = true;
                                curAdapter.putSelectedItem(i, item);
                            }
                        }
                        curAdapter.notifyItemRangeChanged(mPreClickedPosition, currentPosition - mPreClickedPosition + 1);
                    }
                } else {
                    curItem.isSelected = true;
                    curAdapter.putSelectedItem(currentPosition, curItem);
                    curAdapter.notifyItemChanged(currentPosition);
                }
            } else if (mPreClickedAdapterIndex > adapterIndex) {
                //跨页操作,坐标都不一致
                //1.已经有批量选择
                int size = getSelectedCount();
                if (size > 1) {
                    clearAllSelected();
                    curItem.isSelected = true;
                    curAdapter.putSelectedItem(currentPosition, curItem);
                    curAdapter.notifyItemChanged(currentPosition);
                } else {
                    preItem.isSelected = false;
                    curItem.isSelected = true;
                    preAdapter.removeSelectedItem(mPreClickedPosition);
                    preAdapter.notifyItemChanged(mPreClickedPosition);
                    curAdapter.putSelectedItem(currentPosition, curItem);
                    curAdapter.notifyItemChanged(currentPosition);
                }


            } else {
                //跨页操作,坐标都不一致
                //mPreClickedAdapterIndex > adapterIndex 开始跨页批量选择
                if (preItem.isSelected) {
                    //上次点击为选中
                    int size = getSelectedCount();
                    if (size > 1) {
                        clearAllSelected();
                        curItem.isSelected = true;
                        curAdapter.putSelectedItem(currentPosition, curItem);
                        curAdapter.notifyItemChanged(currentPosition);
                    } else {
                        //批量选择
                        for (int i = mPreClickedAdapterIndex; i <= adapterIndex; i++) {
                            CalendarAdapter calendarAdapter = mRvAdapters.get(i);
                            if (calendarAdapter != null) {
                                if (i == mPreClickedAdapterIndex) {
                                    //前一次点击的页面刷新大于等于mPreClickedPosition 的item
                                    for (int j = 0, size1 = calendarAdapter.getItemCount(); j < size1; j++) {
                                        if (j >= mPreClickedAdapterIndex) {
                                            CalendarItem item = calendarAdapter.getItem(j);
                                            if (item.isSelectable) {
                                                item.isSelected = true;
                                                calendarAdapter.putSelectedItem(j, item);
                                            }
                                        }
                                    }

                                } else if (i == adapterIndex) {
                                    //后一次点击的页面刷新小于等于currentPosition 的item
                                    for (int j = 0, size1 = calendarAdapter.getItemCount(); j < size1; j++) {
                                        if (j <= currentPosition) {
                                            CalendarItem item = calendarAdapter.getItem(j);
                                            if (item.isSelectable) {
                                                item.isSelected = true;
                                                calendarAdapter.putSelectedItem(j, item);
                                            }
                                        }
                                    }
                                } else {
                                    //其余刷新全部
                                    for (CalendarItem item : calendarAdapter.getData()) {
                                        if (item.isSelectable) {
                                            item.isSelected = true;
                                            calendarAdapter.putSelectedItem(i, item);
                                        }
                                    }
                                }
                                calendarAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                } else {
                    curItem.isSelected = true;
                    curAdapter.putSelectedItem(currentPosition, curItem);
                    curAdapter.notifyItemChanged(currentPosition);
                }
            }

            mPreClickedAdapterIndex = adapterIndex;
            mPreClickedPosition = currentPosition;
        }
    };


    public List<CalendarItem> getSelectedDatas() {
        List<CalendarItem> selectedData = new ArrayList<>();
        for (int i = 0, size = mRvAdapters.size(); i < size; i++) {
            CalendarAdapter calendarAdapter = mRvAdapters.valueAt(i);
            selectedData.addAll(calendarAdapter.getSelectData());
        }
        return selectedData;

    }

    public int getSelectedCount() {
        int count = 0;
        for (int i = 0, size = mRvAdapters.size(); i < size; i++) {
            count += mRvAdapters.valueAt(i).selectedCount();
        }
        return count;
    }

    ArrayList<Integer> mSelectedWeekdays = new ArrayList<>();

    public void setSelectWeekday(ArrayList<Integer> days) {
        mSelectedWeekdays.clear();
        mSelectedWeekdays.addAll(days);
        for (int i = 0, size = mRvAdapters.size(); i < size; i++) {
            mRvAdapters.valueAt(i).setSelectWeekday(days);
        }
    }

    public void clearAllSelected() {
        for (int i = 0, size = mRvAdapters.size(); i < size; i++) {
            CalendarAdapter calendarAdapter = mRvAdapters.valueAt(i);
            if (calendarAdapter.selectedCount() > 0) {
                calendarAdapter.clearAllSelected();
            }
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

    @Override
    public CharSequence getPageTitle(int position) {
        String date = mLists.get(position).get(0).date;
        String title = date.substring(0, 4) + "年" + date.substring(4, 6) + "月";
        return title;
    }
}
