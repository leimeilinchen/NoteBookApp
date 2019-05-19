package com.necer.listener;
import com.necer.entity.NDate;

/**
 * Created by lml
 */

public interface OnCalendarChangedListener {
    void onCalendarDateChanged(NDate date,boolean isClick);

    void onCalendarStateChanged(boolean isMonthSate);

}
