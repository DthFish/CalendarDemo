package com.dthfish.calendar.horizontal.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dthfish.calendar.R;
import com.dthfish.calendar.horizontal.adapter.MonthAdapter;
import com.dthfish.calendar.horizontal.presenter.HorizontalCalendarPresenter;
import com.dthfish.calendar.CalendarItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Description ${Desc}
 * Author
 * Date 2017/3/8.
 */

public class HorizontalCalendarActivity extends AppCompatActivity implements IHorizontalView {

    private ViewPager mVp;
    private MonthAdapter mAdapter;
    private HorizontalCalendarPresenter mPresenter;
    private Button mBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_calendar);

        initView();
        initEvent();
    }

    private void initEvent() {
        mVp.setAdapter(mAdapter= new MonthAdapter(this));
        mPresenter = new HorizontalCalendarPresenter(this, this);
        mPresenter.getCalendarData(2017,0,2018,0);
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(7);
        mAdapter.setSelectWeekday(integers);
        List<CalendarItem> calendarItems = mAdapter.getLists().get(mVp.getCurrentItem());
    }

    private void initView() {
        mVp = (ViewPager) findViewById(R.id.vp);
        mBtn = (Button) findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CalendarItem> calendarItems = mAdapter.getLists().get(mVp.getCurrentItem());
                CalendarItem calendarItem = calendarItems.get(8);
                Toast.makeText(HorizontalCalendarActivity.this, "" + calendarItem.date, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void initData(List<List<CalendarItem>> data) {
        mAdapter.setLists(data);
    }
}
