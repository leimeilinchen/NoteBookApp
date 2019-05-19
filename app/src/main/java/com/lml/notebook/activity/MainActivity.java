package com.lml.notebook.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TabHost;


import com.lml.notebook.R;
import com.lml.notebook.bean.EventBusBean;
import com.lml.notebook.db.DemoApplication;
import com.lml.notebook.util.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;



public class MainActivity extends TabActivity implements RadioGroup.OnCheckedChangeListener {

    private static final String TAB_TAG_CHART = "iChart";
    private static final String TAB_TAG_DATAS = "iDatas";
    private static final String TAB_TAG_SQUARE = "iSquare";
    private static final String TAB_TAG_MORE = "iMore";
    private TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        DemoApplication.addActivity(this);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        // find view 底部导航栏

        RadioGroup mMainTab = (RadioGroup) findViewById(R.id.tabs_rg);
        mMainTab.setOnCheckedChangeListener(this);

        mTabHost = getTabHost();
        Intent iPlan = new Intent(this, AccountingActivity.class);
        mTabHost.addTab(mTabHost.newTabSpec(TAB_TAG_CHART)
                .setIndicator(getResources().getString(R.string.menu_plan), getResources().getDrawable(R.drawable.tab_record_selector))
                .setContent(iPlan));

        Intent iDatas = new Intent(this, BianQianActivity.class);
        mTabHost.addTab(mTabHost.newTabSpec(TAB_TAG_DATAS)
                .setIndicator(getResources().getString(R.string.menu_data), getResources().getDrawable(R.drawable.tab_record_selector))
                .setContent(iDatas));

        Intent iSquare = new Intent(this, RiLiActivity.class);
        mTabHost.addTab(mTabHost.newTabSpec(TAB_TAG_SQUARE)
                .setIndicator(getResources().getString(R.string.menu_more), getResources().getDrawable(R.drawable.tab_contact_selector))
                .setContent(iSquare));

        Intent iMore = new Intent(this, SetingActivity.class);
        mTabHost.addTab(mTabHost.newTabSpec(TAB_TAG_MORE)
                .setIndicator(getResources().getString(R.string.menu_more), getResources().getDrawable(R.drawable.tab_setting_selector))
                .setContent(iMore));

    }

    @Subscribe
    public void onAddThumEvent(EventBusBean event) {
        if (event.getTag().equals("0")) {
            nightMode();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void nightMode() {
        Preferences.saveNightMode(!Preferences.isNightMode());
        recreate();
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.today_tab:
                this.mTabHost.setCurrentTabByTag(TAB_TAG_CHART);
                break;
            case R.id.record_tab:
                this.mTabHost.setCurrentTabByTag(TAB_TAG_DATAS);
                break;
            case R.id.contact_tab:
                this.mTabHost.setCurrentTabByTag(TAB_TAG_SQUARE);
                break;
            case R.id.settings_tab:
                this.mTabHost.setCurrentTabByTag(TAB_TAG_MORE);
                break;
            default:
                break;
        }
    }


}
