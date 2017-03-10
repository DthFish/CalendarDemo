package com.dthfish.calendar.horizontal.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.dthfish.calendar.CalendarItem;
import com.dthfish.calendar.R;

import java.util.ArrayList;
import java.util.List;



public class CalendarAdapter extends RecyclerView.Adapter {
    private final int mAdapterIndex;
    private LayoutInflater mInflater;
    private Context mContext;
    private List<CalendarItem> mData = new ArrayList<>();
//    private int mPreviousPosition = -1;//之前点击过的坐标

    private SparseArray<CalendarItem> mSelectedItems = new SparseArray<>();
    /**
     * 1-7 用于控制周几的筛选
     */
    private ArrayList<Integer> mSelectedWeekdays = new ArrayList<>();

    public CalendarAdapter(Context context,int adapterIndex) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        for (int i = 1; i <= 7; i++) {
            mSelectedWeekdays.add(i);
        }
        mAdapterIndex = adapterIndex;
    }

    public void setData(List<CalendarItem> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case CalendarItem.TYPE_MONTH:
                View monthView = mInflater.inflate(R.layout.item_calendar_type_month, parent, false);
                viewHolder = new TitleViewHolder(monthView);
                break;
            case CalendarItem.TYPE_DAY:
                View dayView = mInflater.inflate(R.layout.item_calendar_type_day, parent, false);
                viewHolder = new DayViewHolder(dayView);
                break;
            case CalendarItem.TYPE_SPACE:
                View spaceView = mInflater.inflate(R.layout.item_calendar_type_space, parent, false);
                viewHolder = new SpaceViewHolder(spaceView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final CalendarItem item = getItem(position);
        switch (getItemViewType(position)) {
            case CalendarItem.TYPE_MONTH:
                TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
                titleViewHolder.mTvTitle.setText(item.date.substring(0, 4) + "年" + item.date.substring(4, 6) + "月");
                break;
            case CalendarItem.TYPE_DAY:
                DayViewHolder dayViewHolder = (DayViewHolder) holder;
                dayViewHolder.mCtvDay.setText(item.date.substring(6));
                if (!item.isSelectable) {
                    dayViewHolder.mView.setBackgroundColor(Color.parseColor("#dddddd"));
                } else if (item.isSelected && mSelectedWeekdays.contains(item.getWeekday())) {
                    dayViewHolder.mView.setBackgroundColor(Color.parseColor("#ff0000"));
                } else if (item.isSelected && !mSelectedWeekdays.contains(item.getWeekday())) {
                    dayViewHolder.mView.setBackgroundColor(Color.parseColor("#FF7F00"));
                } else {
                    dayViewHolder.mView.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                dayViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (null != mItemClickListener && item.isSelectable) {
                            mItemClickListener.onItemClick(/*mPreviousPosition,*/ position,mAdapterIndex);
//                            mPreviousPosition = position;
                            //可以选择的时候才记录
                        }
                    }
                });

                break;
            case CalendarItem.TYPE_SPACE:
                break;
        }
    }

    public CalendarItem getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    public List<CalendarItem> getData() {
        return mData;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getItemType();
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {

        public TextView mTvTitle;

        public TitleViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public CheckedTextView mCtvDay;

        public DayViewHolder(View itemView) {
            super(itemView);
            mCtvDay = (CheckedTextView) itemView.findViewById(R.id.ctv_day);
            mView = itemView.findViewById(R.id.ll_container);
        }
    }

    static class SpaceViewHolder extends RecyclerView.ViewHolder {

        public SpaceViewHolder(View itemView) {
            super(itemView);
        }
    }

    private OnItemClickListener mItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(/*int previousPosition,*/ int currentPosition,int adapterIndex);
    }

    public void setSelectWeekday(ArrayList<Integer> days) {
        if (days == null) {
            return;
        }
        boolean shouldUpdate = false;
        if (mSelectedWeekdays.size() == days.size()) {
            for (int i = 0; i < mSelectedWeekdays.size(); i++) {
                if (!mSelectedWeekdays.contains(days.get(i))) {
                    shouldUpdate = true;
                    break;
                }
            }
        } else {
            shouldUpdate = true;
        }

        if (shouldUpdate) {
            mSelectedWeekdays.clear();
            mSelectedWeekdays.addAll(days);
            notifyDataSetChanged();
        }
    }

    public List<Integer> getSelectedWeekdays() {

        return mSelectedWeekdays;
    }

    public void putSelectedItem(int key,CalendarItem item){
        mSelectedItems.put(key,item);
    }

    public void removeSelectedItem(int key){
        mSelectedItems.remove(key);
    }

    public int selectedCount(){
        return mSelectedItems.size();
    }

    public void clearAllSelected(){
        for(int i =0,size = selectedCount();i < size; i++){
            CalendarItem item = mSelectedItems.valueAt(i);
            item.isSelected = false;
        }

        //后期可能涉及筛选所以范围尽量大
        int startIndex = mSelectedItems.keyAt(0);
        int index = selectedCount() - 1;
        if (index < 0) {
            return;
        }
        int endIndex = mSelectedItems.keyAt(index);
        mSelectedItems.clear();
        int itemCount = endIndex - startIndex + 1;
        if (startIndex < 0 || endIndex < 0 || itemCount < 0) {
            return;
        }
        notifyItemRangeChanged(startIndex, itemCount);
    }

    public List<CalendarItem> getSelectData(){
        ArrayList<CalendarItem> selectedCalendarItems = new ArrayList<>();
        for (int i = 0; i < selectedCount(); i++) {
            CalendarItem item = mSelectedItems.valueAt(i);
            if (getSelectedWeekdays().contains(item.getWeekday())) {
                selectedCalendarItems.add(item);
            }
        }
        return  selectedCalendarItems;
    }


}
