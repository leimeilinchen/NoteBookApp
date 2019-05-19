package com.lml.notebook.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import com.necer.MyLog;
import com.necer.calendar.Miui9Calendar;
import com.necer.entity.NDate;
import com.necer.listener.OnCalendarChangedListener;
import com.lml.notebook.R;
import com.lml.notebook.adapter.AAAdapter;
import com.lml.notebook.db.DemoApplication;

public class RiLiActivity extends BaseActivity {
    private Miui9Calendar miui9Calendar;

    private TextView tv_month;  //公历月份
    private TextView tv_year;   //公历年份
    private TextView tv_lunar;  //农历日期
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ri_li);
        DemoApplication.addActivity(this);
        miui9Calendar = findViewById(R.id.miui9Calendar);
        tv_month = findViewById(R.id.tv_month);
        tv_year = findViewById(R.id.tv_year);
        tv_lunar = findViewById(R.id.tv_lunar);


        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AAAdapter aaAdapter = new AAAdapter(this);
        recyclerView.setAdapter(aaAdapter);


        miui9Calendar.setOnCalendarChangedListener(new OnCalendarChangedListener() {
            @Override
            public void onCalendarDateChanged(NDate date, boolean isClick) {
                MyLog.d("date:::" + date.localDate);
                MyLog.d("date:::" + date.lunar.lunarYearStr);
                MyLog.d("date:::" + date.lunar.lunarMonthStr);
                MyLog.d("date:::" + date.lunar.lunarDayStr);
                MyLog.d("date:::" + date.lunar.isLeap);
                MyLog.d("date:::" + date.lunar.leapMonth);

                tv_month.setText(date.localDate.getMonthOfYear() + "月");
                tv_year.setText(date.localDate.getYear() + "年");

                tv_lunar.setText(date.lunar.lunarYearStr + date.lunar.lunarMonthStr + date.lunar.lunarDayStr);


                MyLog.d("isClickisClickisClick:::" + isClick);

            }

            @Override
            public void onCalendarStateChanged(boolean isMonthSate) {
                MyLog.d("OnCalendarChangedListener:::" + isMonthSate);
            }
        });


        tv_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miui9Calendar.toToday();

            }
        });


    }
}
