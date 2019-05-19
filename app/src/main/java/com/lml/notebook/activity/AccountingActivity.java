package com.lml.notebook.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;
import com.lml.notebook.R;
import com.lml.notebook.adapter.MainViewPagerAdapter;
import com.lml.notebook.db.DemoApplication;
import com.lml.notebook.util.DateUtil;
import com.lml.notebook.util.GlobalUtil;

public class AccountingActivity extends BaseActivity implements ViewPager.OnPageChangeListener{

    private static final String TAG ="MainActivity";

    private ViewPager viewPager;
    private MainViewPagerAdapter pagerAdapter;
    private TickerView amountText;//实现钱数转换动画
    private TextView dateText;
    private int currentPagerPosition = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_accouting);
        DemoApplication.addActivity(this);
        sharedPreferences = getSharedPreferences(this.getPackageName(), this.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (!TextUtils.isEmpty(sharedPreferences.getString("phone", null))) {
            name=sharedPreferences.getString("phone", null);
        }
        GlobalUtil.getInstance().setContext(getApplicationContext());
        GlobalUtil.getInstance().mainActivity = this;
        getSupportActionBar().setElevation(0);

        amountText = (TickerView)findViewById(R.id.amount_text);
        amountText.setCharacterLists(TickerUtils.provideNumberList());
        dateText=(TextView) findViewById(R.id.date_text);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(),name);
        pagerAdapter.notifyDataSetChanged();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(this);
        viewPager.setCurrentItem(pagerAdapter.getLatsIndex());

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            //点击实现钱数动画转换
            public void onClick(View v) {
                Intent intent = new Intent(AccountingActivity.this,AddRecordActivity.class);
                startActivityForResult(intent,1);
            }
        });
        updateHeader();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pagerAdapter.reload();
        updateHeader();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG,"cost: "+pagerAdapter.getTotalCost(position));
        currentPagerPosition = position;
        updateHeader();
    }

    public void updateHeader(){
        String amount = String.valueOf(pagerAdapter.getTotalCost(currentPagerPosition));
        amountText.setText(amount);
        String date = pagerAdapter.getDateStr(currentPagerPosition);
        dateText.setText(DateUtil.getWeekDay(date));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
