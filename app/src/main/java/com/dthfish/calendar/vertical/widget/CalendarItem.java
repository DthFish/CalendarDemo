package com.dthfish.calendar.vertical.widget;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class CalendarItem {

    public static final int TYPE_MONTH = 30;
    public static final int TYPE_DAY = 10;
    public static final int TYPE_SPACE = 0;
    @ItemType
    private int itemType;//不可以动态修改类型
    public boolean isSelected;//是否已选择
    public boolean isSelectable = true;//是否可选择
    public String date;
    public String otherDesc;//其他描述
    private int weekday;//周日为1，周一为2...0则表示不为day的属性

    public CalendarItem(int itemType, String date) {
        this(itemType,date,0);
    }

    public CalendarItem(int itemType, String date, int weekday){
        this.itemType = itemType;
        this.date = date;
        this.weekday = weekday;
    }

    public int getItemType() {
        return itemType;
    }

    public int getWeekday() {
        return weekday;
    }

    @IntDef(flag = true,value = {TYPE_MONTH,TYPE_DAY,TYPE_SPACE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ItemType{
    }
}
