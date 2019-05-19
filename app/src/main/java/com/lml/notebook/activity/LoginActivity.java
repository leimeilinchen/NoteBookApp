package com.lml.notebook.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.lml.notebook.R;
import com.lml.notebook.bean.UserBean;
import com.lml.notebook.db.DbHelper; //sqlite数据库存储
import com.lml.notebook.db.DemoApplication;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends BaseActivity {

    Button tvLogin;

    LinearLayout layoutLogin;

    EditText tvPhone;

    ImageView ivLoadMima;

    EditText etPwd;

    LinearLayout llPhone;

    TextView btnPassw,btn_register;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String userType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        setContentView(R.layout.activity_login);
        DemoApplication.addActivity(this);
        userType = getIntent().getStringExtra("userType");
        tvLogin = (Button) findViewById(R.id.tv_login);
        layoutLogin = (LinearLayout) findViewById(R.id.layout_login);
        tvPhone = (EditText) findViewById(R.id.tv_phone);
        btn_register = (TextView) findViewById(R.id.btn_register);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        llPhone = (LinearLayout) findViewById(R.id.ll_phone);
        btnPassw = (TextView) findViewById(R.id.btn_passw);
        sharedPreferences = getSharedPreferences(this.getPackageName(), this.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(LoginActivity.this, permissions, 1);
        }
        if (!TextUtils.isEmpty(sharedPreferences.getString("phone", null))) {
            //如果第一次登录过直接进入，无需再重新登录
            String NAME=sharedPreferences.getString("phone", null);
            Intent intentv = new Intent(LoginActivity.this, LoginPassActivity.class);
            if (!TextUtils.isEmpty(sharedPreferences.getString("password", null))){
                intentv.putExtra("TYPE", "1");
            }else {
                intentv.putExtra("TYPE", "0");
            }

            startActivity(intentv);
            finish();
            return;
        }
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               loging();
                //登录


            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //注册

                Intent intents = new Intent(LoginActivity.this, RegisterActivity.class);
                intents.putExtra("userType", userType);
                intents.putExtra("PASS", "0");
                startActivity(intents);
            }
        });
        btnPassw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //忘记密码
                Intent intents = new Intent(LoginActivity.this, RegisterActivity.class);
                intents.putExtra("userType", userType);
                intents.putExtra("PASS", "1");
                startActivity(intents);
            }
        });
    }


    private void loging() {
        if (TextUtils.isEmpty(tvPhone.getText().toString())) {
            Toast.makeText(LoginActivity.this, "用户名不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(etPwd.getText().toString())) {
            Toast.makeText(LoginActivity.this, "密码不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }

        String stuNmNo = tvPhone.getText().toString();
        String[] args = new String[]{stuNmNo};
        UserBean userBean = DbHelper.getInstance(this).findUser(args);//sqlite数据库存储
        if (TextUtils.isEmpty(userBean.getNickName()) || TextUtils.isEmpty(userBean.getNickName())) {
            Toast.makeText(LoginActivity.this, "此用户不存在!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (userBean.getPassword().equals(etPwd.getText().toString())) {
                editor.putString("phone", stuNmNo);
                editor.commit();
                Toast.makeText(LoginActivity.this, "登陆成功!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "密码错误!", Toast.LENGTH_SHORT).show();
            }

        }
    }


}
