package com.lml.notebook.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lml.notebook.R;
import com.lml.notebook.bean.UserBean;
import com.lml.notebook.db.DbHelper; //sqlite数据库存储
import com.lml.notebook.db.DemoApplication;


public class RegisterActivity extends BaseActivity {

    Button tvLogin;
    LinearLayout layoutLogin;
    EditText tvPhone;
    ImageView ivLoadMima;
    EditText etPwd;
    EditText etCompwd;
    LinearLayout llPhone;
    Button checkbox2;
    LinearLayout select;
    private String userType;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        setContentView(R.layout.activity_register);
        DemoApplication.addActivity(this);
        userType = getIntent().getStringExtra("userType");
        pass = getIntent().getStringExtra("PASS");
        tvLogin = (Button) findViewById(R.id.tv_login);
        layoutLogin = (LinearLayout) findViewById(R.id.layout_login);
        tvPhone = (EditText) findViewById(R.id.tv_phone);

        etPwd = (EditText) findViewById(R.id.et_pwd);

        llPhone = (LinearLayout) findViewById(R.id.ll_phone);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(tvPhone.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "用户名不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(etPwd.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "密码不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }


                UserBean userBean = new UserBean();
                userBean.setNickName(tvPhone.getText().toString());
                userBean.setPassword(etPwd.getText().toString());
                boolean hasUser = DbHelper.getInstance(RegisterActivity.this)//sqlite数据库存储
                        .saveUser(userBean);
                if (hasUser) {
                    Toast.makeText(RegisterActivity.this, "该信息已注册！", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (!TextUtils.isEmpty(sharedPreferences.getString("phone", null))) {
                        editor.remove("phone");
                    }
                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }




}
