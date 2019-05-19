package com.lml.notebook.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.lml.notebook.R;
import com.lml.notebook.adapter.NoteDataAdapter;
import com.lml.notebook.db.DbHelper;  //sqlite数据库存储
import com.lml.notebook.db.DemoApplication;
import com.lml.notebook.db.Note;

import java.util.List;


public class BianQianActivity extends BaseActivity {
    // 意图请求代码的常量
    public static final int ADD_NOTE_REQUEST = 1;

    private NoteDataAdapter mAdapter;
    private ListView mlistview;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bian_qian);
        sharedPreferences = getSharedPreferences(this.getPackageName(), this.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (!TextUtils.isEmpty(sharedPreferences.getString("phone", null))) {
            name=sharedPreferences.getString("phone", null);
        }
        DemoApplication.addActivity(this);
        mlistview = findViewById(R.id.recycler_view);
        // 参考浮动添加注释按钮
        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_note);
        // 单击该按钮的单击侦听器
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 这可以用作上下文，因为它从onclick侦听器获取此上下文
                // 这里的Intent是打开添加注释活动
                Intent intent = new Intent(BianQianActivity.this, AddNoteActivity.class);
                //使用此方法允许从活动返回结果。
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });
        //获取数据库保存数据

        data();


    }

    // 当添加注释活动关闭时，它将返回值，此覆盖处理这些结果。
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //如果请求代码是添加注释请求，结果代码是ok，则为true。
        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            // 从添加注释活动中获取额外内容。
            String title = data.getStringExtra(AddNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY, 1);
            String path =data.getStringExtra(AddNoteActivity.EXTRA_PATH);
                    // 注意实例，从extras传递值
                    Note note = new Note(name,title, description, priority,path);
            // 通过视图模型插入方法插入数据库。
            DbHelper.getInstance(this).saveNote(note);//sqlite数据库存储
            data();
            // 反馈信息
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            // 否则结果不合适
        } else {
            // 反馈信息
            Toast.makeText(this, "没有保存", Toast.LENGTH_SHORT).show();
        }
    }

    // 插入三点菜单选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 实例化一个MenuInflater实例
        MenuInflater menuInflater = getMenuInflater();
        // 使用它传递给main_menu，使用传递给此方法的菜单值。
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // 用于在选择其中一个菜单选项时进行处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 关于商品ID的switch语句
        switch (item.getItemId()) {
            // 如果选择删除所有备注
            case R.id.delete_all_notes:
                // 通过视图模型删除所有笔记
                DbHelper.getInstance(this).deleteAll();
                data();


                // 反馈信息
                Toast.makeText(this, "已删除所有便签", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void data() {
        final List<Note> mlist = DbHelper.getInstance(this).getAllHeart(name);//sqlite数据库
        mAdapter = new NoteDataAdapter(BianQianActivity.this, mlist);
        mlistview.setAdapter(mAdapter);
        mlistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BianQianActivity.this);
                builder.setTitle("确定删除？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                DbHelper.getInstance(BianQianActivity.this).deleteRecord(mlist.get(position).getId());
                                mlist.remove(position);
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                builder.create();
                builder.show();
                return true;
            }
        });
        mAdapter.notifyDataSetChanged();

    }
}
