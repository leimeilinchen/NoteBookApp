package com.lml.notebook.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.lml.notebook.R;
import com.lml.notebook.db.DemoApplication;
import com.lml.notebook.util.SecurityCodeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
//锁屏码
public class LoginPassActivity extends BaseActivity implements SecurityCodeView.InputCompleteListener {


    @BindView(R.id.edit_security_code)
    SecurityCodeView editSecurityCode;
    @BindView(R.id.tv_mima)
    Button tvMima;
    private String passCode, type;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int error = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pass);
        ButterKnife.bind(this);
        DemoApplication.addActivity(this);
        type = getIntent().getStringExtra("TYPE");
        setListener();
        sharedPreferences = getSharedPreferences(this.getPackageName(), this.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (TextUtils.isEmpty(sharedPreferences.getString("password", null))) {
            //如果第一次登录,需要设置密码
            Toast.makeText(LoginPassActivity.this, "请设置四位密码，在登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (type.equals("0")) {
            tvMima.setText("保存");
        } else {
            tvMima.setText("登录");
        }


    }

    @OnClick(R.id.tv_mima)
    public void onViewClicked() {
        if (TextUtils.isEmpty(passCode)) {
            Toast.makeText(LoginPassActivity.this, "请输入四位密码", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (passCode.length() == 4) {
                if (type.equals("0")) {
                    editor.remove("password").commit();
                    editor.putString("password", passCode);
                    editor.commit();
                    startActivity(new Intent(LoginPassActivity.this, MainActivity.class));
                    finish();
                } else if (type.equals("1")) {
                    if (passCode.equals(sharedPreferences.getString("password", null))) {

                        startActivity(new Intent(LoginPassActivity.this, MainActivity.class));
                        return;
                    } else {
                        String mi=sharedPreferences.getString("password", null);

                        if (error == 3) {
                            startActivity(new Intent(LoginPassActivity.this, LoginActivity.class));
                            editor.remove("phone").commit();
                            Toast.makeText(LoginPassActivity.this, "密码错误三次，请重新登录", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            error++;
                            Toast.makeText(LoginPassActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                        }

                        return;
                    }

                }


            } else {
                Toast.makeText(LoginPassActivity.this, "请输入四位密码", Toast.LENGTH_SHORT).show();
                return;
            }

        }
    }

    private void setListener() {
        editSecurityCode.setInputCompleteListener(this);
    }

    @Override
    public void inputComplete() {
        //Toast.makeText(getApplicationContext(), "验证码是：" + editSecurityCode.getEditContent(), Toast.LENGTH_LONG).show();
        if (!TextUtils.isEmpty(editSecurityCode.getEditContent())) {
            passCode = editSecurityCode.getEditContent();
        }
    }

    @Override
    public void deleteContent(boolean isDelete) {
        if (isDelete) {

        }
    }
}
