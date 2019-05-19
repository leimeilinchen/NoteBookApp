package com.lml.notebook.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;

import com.lml.notebook.R;
import com.lml.notebook.util.Preferences;


/**
 * 基类<br>
 * 如果继承本类，需要在 layout 中添加 {@link Toolbar} ，并将 AppTheme 继承 Theme.AppCompat.NoActionBar 。
 * Created
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Preferences.isNightMode()) {
            setTheme(getDarkTheme());
        }

        super.onCreate(savedInstanceState);

    }

    @StyleRes
    protected int getDarkTheme() {
        return R.style.AppThemeDark;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);

    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);

    }













    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
