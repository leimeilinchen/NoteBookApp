package com.necer.calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public abstract class MiuiCalendar extends NCalendar{

    public MiuiCalendar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onAutoToMonthState() {
        monthCalendar.autoToMonth();
        childLayout.autoToMonth();
    }

    @Override
    protected void onAutoToWeekState() {
        monthCalendar.autoToMIUIWeek();
        childLayout.autoToWeek();
    }

    @Override
    protected float getMonthYOnWeekState() {
        return -monthCalendar.getMonthCalendarOffset();
    }

    @Override
    protected void onSetWeekVisible(int dy) {

        if (childLayout.isWeekState() ) {
            weekCalendar.setVisibility(VISIBLE);
            monthCalendar.setVisibility(INVISIBLE);
        } else  {
            weekCalendar.setVisibility(INVISIBLE);
            monthCalendar.setVisibility(VISIBLE);
        }
    }

}
