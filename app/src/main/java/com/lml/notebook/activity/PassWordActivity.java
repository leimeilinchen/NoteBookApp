package com.lml.notebook.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.lml.notebook.R;
import com.lml.notebook.bean.UserBean;
import com.lml.notebook.db.DbHelper;  //sqlite数据库
import com.lml.notebook.db.DemoApplication;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
//账号密码修改
public class PassWordActivity extends BaseActivity {

    @BindView(R.id.tv_login)
    Button tvLogin;
    @BindView(R.id.layout_login)
    LinearLayout layoutLogin;
    @BindView(R.id.zhanhao)
    TextView zhanhao;
    @BindView(R.id.tv_phone)
    EditText tvPhone;
    @BindView(R.id.mima)
    TextView mima;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.ll_phone)
    LinearLayout llPhone;
    private String type;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String user;

    UserBean userBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_word);
        DemoApplication.addActivity(this);
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences(this.getPackageName(), this.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (!TextUtils.isEmpty(sharedPreferences.getString("phone", null))) {
            user=sharedPreferences.getString("phone", null);
            userBean= DbHelper.getInstance(PassWordActivity.this).findUser(new String[]{user});


        }
        type = getIntent().getStringExtra("ZHANGHAO");

        if (type.equals("0")) {
            zhanhao.setText("旧账号");
            mima.setText("新账号");
            tvPhone.setHint("请输入账号");
            etPwd.setHint("请输入账号");

        } else if (type.equals("1")) {
            zhanhao.setText("旧密码");
            mima.setText("新密码");
            tvPhone.setHint("请输入密码");
            etPwd.setHint("请输入密码");
        }
    }

    @OnClick(R.id.tv_login)
    public void onClick() {
        if (type.equals("0")) {
            UserBean users=new UserBean();
            users.setId(userBean.getId());
            users.setNickName(etPwd.getText().toString());
            users.setPassword(userBean.getPassword());
            DbHelper.getInstance(PassWordActivity.this).updateUser(users);
            Toast.makeText(PassWordActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
            finish();
//            editor.remove("phone").commit();
//            startActivity(new Intent(PassWordActivity.this,LoginActivity.class));
//            DemoApplication.finishActivity();
        } else if (type.equals("1")) {
            UserBean us=new UserBean();
            us.setId(userBean.getId());
            us.setNickName(userBean.getNickName());
            us.setPassword(etPwd.getText().toString());
            DbHelper.getInstance(PassWordActivity.this).updateUser(us);
            //editor.remove("phone").commit();
            Toast.makeText(PassWordActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
            finish();
//            startActivity(new Intent(PassWordActivity.this,LoginActivity.class));
//            DemoApplication.finishActivity();
        }
    }
}
