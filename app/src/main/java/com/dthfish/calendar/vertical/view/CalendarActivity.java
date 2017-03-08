package com.dthfish.calendar.vertical.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.dthfish.calendar.R;
import com.dthfish.calendar.vertical.IRoomStatusView;
import com.dthfish.calendar.vertical.presenter.RoomStatusPresenter;
import com.dthfish.calendar.vertical.widget.CalendarItem;
import com.dthfish.calendar.vertical.widget.CalendarView;

import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends AppCompatActivity implements IRoomStatusView, CompoundButton.OnCheckedChangeListener {

    private CalendarView mCalendarView;
    private RoomStatusPresenter mRoomStatusPresenter;
    private CheckBox mCbAll;
    private CheckBox mCbMonday;
    private CheckBox mCbTuesday;
    private CheckBox mCbWednesday;
    private CheckBox mCbThursday;
    private CheckBox mCbFriday;
    private CheckBox mCbSaturday;
    private CheckBox mCbSunday;
    private List<CheckBox> mCheckBoxes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initView();
        initEvent();
    }

    private void initEvent() {
        mCheckBoxes.add(mCbAll);
        mCheckBoxes.add(mCbSunday);
        mCheckBoxes.add(mCbMonday);
        mCheckBoxes.add(mCbTuesday);
        mCheckBoxes.add(mCbWednesday);
        mCheckBoxes.add(mCbThursday);
        mCheckBoxes.add(mCbFriday);
        mCheckBoxes.add(mCbSaturday);

        mCbAll.setOnCheckedChangeListener(this);
        mCbMonday.setOnCheckedChangeListener(this);
        mCbTuesday.setOnCheckedChangeListener(this);
        mCbWednesday.setOnCheckedChangeListener(this);
        mCbThursday.setOnCheckedChangeListener(this);
        mCbFriday.setOnCheckedChangeListener(this);
        mCbSaturday.setOnCheckedChangeListener(this);
        mCbSunday.setOnCheckedChangeListener(this);

        mRoomStatusPresenter = new RoomStatusPresenter(this, this);
        mRoomStatusPresenter.start();

    }

    private void initView() {
        mCalendarView = (CalendarView) findViewById(R.id.cv);
        mCbAll = (CheckBox) findViewById(R.id.cb_all);
        mCbMonday = (CheckBox) findViewById(R.id.cb_monday);
        mCbTuesday = (CheckBox) findViewById(R.id.cb_tuesday);
        mCbWednesday = (CheckBox) findViewById(R.id.cb_wednesday);
        mCbThursday = (CheckBox) findViewById(R.id.cb_thursday);
        mCbFriday = (CheckBox) findViewById(R.id.cb_friday);
        mCbSaturday = (CheckBox) findViewById(R.id.cb_saturday);
        mCbSunday = (CheckBox) findViewById(R.id.cb_sunday);

        findViewById(R.id.btn_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<CalendarItem> selectDates = mCalendarView.getSelectDates();
                Log.d("dddd", "get: -------------start--------------");
                for (int i = 0; i < selectDates.size(); i++) {
                    CalendarItem item = selectDates.get(i);
                    Log.d("dddd", "get: "+ item.date);
                }
                Log.d("dddd", "get: -------------end--------------");
            }
        });

    }

    @Override
    public void setCalendarData(List<CalendarItem> data) {
        mCalendarView.setData(data);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        ArrayList<Integer> checked = new ArrayList<>();
        for (int i = 0; i < mCheckBoxes.size(); i++) {
            if (mCheckBoxes.get(i).isChecked()) {
                checked.add(i);
            }
        }
        if (checked.contains(0)) {
            checked.clear();
            for (int i = 1; i <= 7; i++) {
                checked.add(i);
            }
            mCalendarView.selectedWeekday(checked);
        } else {
            mCalendarView.selectedWeekday(checked);
        }
    }
}
